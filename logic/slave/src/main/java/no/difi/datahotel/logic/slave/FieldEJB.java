package no.difi.datahotel.logic.slave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Fields;
import no.difi.datahotel.util.bridge.Metadata;

@Stateless
public class FieldEJB {
	
	private static Logger logger = Logger.getLogger(FieldEJB.class.getSimpleName());
	
	private HashMap<String, List<Field>> fields = new HashMap<String, List<Field>>();
	private HashMap<String, Definition> definitions = new HashMap<String, Definition>();
	private HashMap<String, List<String>> defUsage = new HashMap<String, List<String>>();
	
	public void update(Metadata metadata) {
		logger.info("[" + metadata.getLocation() + "] Reading fields.");
		
		if (fields.containsKey(metadata.getLocation())) {
			for (Field f : fields.get(metadata.getLocation())) {
				Definition d = f.getDefinition();
				defUsage.get(d.getShortName()).remove(metadata.getLocation());
			}
		}
		
		Fields fresh = Fields.read(metadata.getLocation());
		
		fields.put(metadata.getLocation(), fresh.getFields());
		for (Field f : fresh.getFields()) {
			Definition d = f.getDefinition();
			if (!definitions.containsKey(d.getShortName()))
				definitions.put(d.getShortName(), d);
			if (!defUsage.containsKey(d.getShortName()))
				defUsage.put(d.getShortName(), new ArrayList<String>());
			if (!defUsage.get(d.getShortName()).contains(metadata.getLocation()))
				defUsage.get(d.getShortName()).add(metadata.getLocation());
		}
	}
	
	public List<Field> getFields(String owner, String group, String dataset) {
		String location = owner + "/" + group + "/" + dataset;
		return fields.get(location);
	}
	
	public List<Definition> getDefinitions() {
		return new ArrayList<Definition>(definitions.values());
	}
	
	public Definition getDefinition(String def) {
		return definitions.get(def);
	}
	
	public List<String> getUsage(String definition) {
		return defUsage.get(definition);
	}
}