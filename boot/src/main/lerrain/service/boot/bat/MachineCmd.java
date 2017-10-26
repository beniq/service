package lerrain.service.boot.bat;

import lerrain.service.boot.Machine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by lerrain on 2017/10/24.
 */
public class MachineCmd
{
    Machine machine;

    public MachineCmd(Machine machine)
    {
        this.machine = machine;
    }

    public void runScript(String str) throws Exception
    {
        for (String cmd : str.split("\n"))
            runCommand(cmd);
    }

    public void runCommand(String str) throws Exception
    {
        if (str == null)
            return;

        if (str.startsWith("MKDIR"))
        {
            String arg = str.substring(6);
            machine.run("mkdir -p " + machine.getPath(arg));
        }
        else if (str.startsWith("UPLOAD"))
        {
            String fileStr = str.substring(7);
            String[] ff = fileStr.trim().split(" ");

            File f = new File(ff[0]);
            if (f.isFile())
            {
                try (InputStream is = new FileInputStream(f))
                {
                    int p = Math.max(ff[1].lastIndexOf("/"), ff[1].lastIndexOf("\\"));
                    machine.upload(is, ff[1].substring(p + 1), f.length(), ff[1].substring(0, p));
                }
            }
        }
    }
}
