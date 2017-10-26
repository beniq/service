package lerrain.service.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class MachineMgr
{
    @Autowired
    MachineDao machineDao;

    Map<Integer, Machine> map;

    Map<Long, String> cmds;

    public void reset()
    {
        map = new HashMap<>();

        for (Machine m : machineDao.loadMachineList())
            map.put(m.getIndex(), m);

        cmds = machineDao.loadAllCommand();
    }

    public String runCommand(int index, Long commandId) throws Exception
    {
        return getMachine(index).run(cmds.get(commandId));
    }

    public Machine getMachine(int index)
    {
        return map.get(index);
    }

    public Collection<Machine> getAllMachines()
    {
        return map.values();
    }
}
