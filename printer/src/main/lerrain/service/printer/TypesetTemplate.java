package lerrain.service.printer;

import lerrain.tool.Common;
import lerrain.tool.document.typeset.Typeset;
import lerrain.tool.document.typeset.TypesetUtil;

import java.io.File;
import java.util.Map;

/**
 * Created by lerrain on 2017/4/23.
 */
public class TypesetTemplate
{
    Long id;
    Long signId;

    String code;
    String name;

    String workDir;
    String testFile;
    String templateFile;

    Typeset typeset;

    File test;

    public String getTemplateFile()
    {
        return templateFile;
    }

    public void setTemplateFile(String templateFile)
    {
        this.templateFile = templateFile;
    }

    public File getTest()
    {
        return test;
    }

    public String getTestFile()
    {
        return testFile;
    }

    public void setTestFile(String testFile)
    {
        this.testFile = testFile;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getWorkDir()
    {
        return workDir;
    }

    public void setWorkDir(String workDir)
    {
        this.workDir = workDir;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Long getSignId()
    {
        return signId;
    }

    public void setSignId(Long signId)
    {
        this.signId = signId;
    }

    public Typeset getTypeset()
    {
        return typeset;
    }

    public void refresh()
    {
        if (!Common.isEmpty(this.getTestFile()))
            test = (new File(Common.pathOf(TypesetUtil.getResourcePath(), Common.pathOf(workDir, testFile))));
        else
            test = null;

        if (!Common.isEmpty(templateFile))
        {
            System.out.println("loading... " + id + "/" + code + "/" + name + "/" + templateFile);

            try
            {
                this.typeset = TypesetUtil.parseTypeset(Common.pathOf(TypesetUtil.getResourcePath(), Common.pathOf(workDir, templateFile)));
                this.typeset.setId(code);
            }
            catch (Exception e)
            {
                this.typeset = null;
                e.printStackTrace();
            }
        }
        else
        {
            this.typeset = null;
        }
    }
}
