package no.difi.datahotel.util.bridge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import no.difi.datahotel.util.shared.Filesystem;

@XmlRootElement
public class Metadata extends Abstract implements Comparable<Metadata>, Light<MetadataLight> {

	private String location;
	private List<Metadata> children = new ArrayList<Metadata>();
	private boolean active = false;
	private boolean dataset;
	private Metadata parent;
	private Logger logger = Logger.getAnonymousLogger();

	// Values for users
	private String shortName;
	private String name;
	private String description;
	private String url;
	private Long updated;

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getUpdated() {
		return updated;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}

	@XmlTransient
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
		this.logger = Logger.getLogger(location);
	}

	@XmlTransient
	public List<Metadata> getChildren() {
		return children;
	}

	public void addChild(Metadata child) {
		this.children.add(child);
		child.parent = this;

		if (child.isActive() && child.updated != null)
			if (this.updated == null || this.updated < child.updated)
				this.updated = child.updated;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@XmlTransient
	public boolean isDataset() {
		return dataset;
	}

	public void setDataset(boolean dataset) {
		this.dataset = dataset;
	}

	@XmlTransient
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@XmlTransient
	public Metadata getParent() {
		return parent;
	}

	public void setParent(Metadata parent) {
		this.parent = parent;
	}

	public MetadataLight light() {
		return new MetadataLight(this);
	}

	public void save() throws Exception {
		save(Filesystem.getFile(Filesystem.FOLDER_SLAVE, location, Filesystem.FILE_METADATA), this);
	}

	public static Metadata read(String location) {
		File folder = Filesystem.getFolder(Filesystem.FOLDER_SLAVE, location);
		Metadata metadata = (Metadata) read(Metadata.class, Filesystem.getFile(folder, Filesystem.FILE_METADATA));
		metadata.setLocation(location);
		metadata.setShortName(folder.getName());
		metadata.setActive(!Filesystem.getFile(folder, Filesystem.INACTIVE).exists());
		metadata.setDataset(Filesystem.getFile(folder, Filesystem.FILE_DATASET).exists());

		return metadata;
	}

	public static String getLocation(String... dir) {
		String location = dir[0];
		for (int i = 1; i < dir.length; i++)
			location += "/" + dir[i];
		return location;
	}

	@Override
	public int compareTo(Metadata other) {
		return String.valueOf(name).compareTo(String.valueOf(other.name));
	}
}