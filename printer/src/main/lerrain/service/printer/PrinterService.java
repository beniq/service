package lerrain.service.printer;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import lerrain.tool.Common;
import lerrain.tool.document.LexDocument;
import lerrain.tool.document.export.Painter;
import lerrain.tool.document.export.PdfPainterNDF;
import lerrain.tool.document.export.PdfPainterSign;
import lerrain.tool.document.typeset.Typeset;
import lerrain.tool.document.typeset.TypesetDocument;
import lerrain.tool.document.typeset.TypesetQrcode;
import lerrain.tool.document.typeset.TypesetUtil;
import lerrain.tool.document.typeset.environment.TextDimensionAwt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.util.*;

@Service
public class PrinterService
{
	Map<Object, TypesetTemplate> map;

	@Autowired
	PrinterDao printerDao;

	@Value("${path.printer}")
	String path;

	@Value("${path.temp}")
	String tempPath;

	Map<String, Sign> signs;
	Map<Long, Painter> pdfPainters;

	@PostConstruct
	private void init()
	{
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);

		TypesetUtil.setResourcePath(Common.pathOf(path, "resource/template"));
		TypesetUtil.setFontPath(Common.pathOf(path, "resource/fonts/"));
		TypesetUtil.setTextDimension(new TextDimensionAwt());
		TypesetUtil.addElementFactory("qrcode", new TypesetQrcode());

		buildPdfPainters();

		reset();
	}

	public Long verify(InputStream is) throws Exception
	{
		PdfReader pdfReader = new PdfReader(is);
		PdfDocument pdfDocument = new PdfDocument(pdfReader);
		SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
		List<String> signedNames = signatureUtil.getSignatureNames();

		for (String signedName : signedNames)
		{
			PdfPKCS7 rs = signatureUtil.verifySignature(signedName, "BC");

			if (!rs.verify())
				throw new RuntimeException(signedName + " not match");

			for (Certificate cert : rs.getCertificates())
			{
				String publicKey = Common.encodeBase64(cert.getPublicKey().getEncoded());
				if (signs.containsKey(publicKey))
					return signs.get(publicKey).getId();
			}
		}

		throw new RuntimeException("PDF验签通过，但签名不再签名库中");
	}

	public void reset()
	{
		map = new HashMap<>();

		List<TypesetTemplate> list = printerDao.loadAll();
		if (list != null) for (TypesetTemplate tt : list)
		{
			tt.refresh();

			map.put(tt.getId(), tt);
			map.put(tt.getCode(), tt);
		}
	}

	public boolean exists(String code)
	{
		if (code == null)
			return false;

		return map.containsKey(code);
	}

	public Long create(String code, String name)
	{
		TypesetTemplate tt = printerDao.create(code, name);

		new File(Common.pathOf(TypesetUtil.getResourcePath(), tt.getWorkDir())).mkdirs();

		synchronized (map)
		{
			map.put(tt.getId(), tt);

			if (!Common.isEmpty(tt.getCode()))
				map.put(tt.getCode(), tt);
		}

		return tt.getId();
	}

	public void resetCode(TypesetTemplate tt, String code)
	{
		synchronized (map)
		{
			if (!Common.isEmpty(tt.getCode()))
				map.remove(tt.getCode());

			if (!Common.isEmpty(code))
			{
				tt.setCode(code);
				map.put(tt.getCode(), tt);
			}
		}
	}

	public void save(TypesetTemplate tt)
	{
		tt.refresh();

		printerDao.save(tt);
	}

	public String getPath(String dir)
{
	return Common.pathOf(path, dir);
	}

	public Collection<TypesetTemplate> list()
	{
		return map.values();
	}

	public TypesetTemplate getTypesetTemplate(Object code)
	{
		synchronized (map)
		{
			return map.get(code);
		}
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public void addTypeset(TypesetTemplate typesetTemplate)
	{
		synchronized (map)
		{
			map.put(typesetTemplate.getCode(), typesetTemplate);
		}
	}

	public void clear()
	{
		synchronized (map)
		{
			map.clear();
		}
	}

	public LexDocument build(TypesetTemplate template, JSONObject param)
	{
		Typeset typeset = template.getTypeset();
		TypesetDocument doc = new TypesetDocument(typeset);

		param.put("FONT_HEI", "simhei.ttf");
		param.put("FONT_KAI", "simkai.ttf");
		param.put("FONT_SONG", "stsong.ttf");
		param.put("FONT_YOU", "simyou.ttf");
		param.put("FONT_CONSOLA", "consola.ttf");
		param.put("FONT_COURIER", "courier.ttf");
		param.put("RESOURCE_PATH", TypesetUtil.getResourcePath() + File.separator + template.getCode() + File.separator);

		doc.build(param);

		return doc;
	}

	public synchronized Painter getPdfSignPainter(TypesetTemplate template)
	{
		if (template.getSignId() == null)
		{
			return pdfPainters.get(0L);
		}
		else
		{
			return pdfPainters.get(template.getSignId());
		}
	}

	private void buildPdfPainters()
	{
		pdfPainters = new HashMap<>();
		pdfPainters.put(0L, new PdfPainterNDF());

		signs = new HashMap<>();

		for (Sign sign : printerDao.loadAllSign())
		{
			String keystore = getPath("resource/sign/") + sign.getKeystore();
			char[] pwd = sign.getPassword().toCharArray();

			try (InputStream keyIs = new FileInputStream(keystore))
			{
				KeyStore ks = KeyStore.getInstance("PKCS12");
				ks.load(keyIs, pwd);
				String alias = ks.aliases().nextElement();
				PrivateKey key = (PrivateKey)ks.getKey(alias, pwd);
				Certificate[] chain = ks.getCertificateChain(alias);

				PublicKey pb = ks.getCertificate(alias).getPublicKey();
				signs.put(Common.encodeBase64(pb.getEncoded()), sign);

				//pdfPainters.put(sign.getId(), new PdfPainterSign2(key, chain, sign.getScope(), sign.getReason(), sign.getLocation(), tempPath));
				pdfPainters.put(sign.getId(), new PdfPainterSign(keystore, sign.getPassword(), sign.getScope(), sign.getReason(), sign.getLocation(), tempPath));
				System.out.println(sign.getScope() + "/" + sign.getKeystore() + " - load success");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public Long match(String key)
	{
		return null;
	}

	public void log(String key, Long templateId, String fileType, int outType, int result, String ip, int buildTime, int exportTime, int pages)
	{
		printerDao.log(key, templateId, fileType, outType, result, ip, buildTime, exportTime, new Date(), pages);
	}
}
