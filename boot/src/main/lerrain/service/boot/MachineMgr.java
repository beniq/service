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

    public void reset()
    {
        map = new HashMap<>();

        for (Machine m : machineDao.loadMachineList())
            map.put(m.getIndex(), m);
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
