package no.difi.datahotel.util.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import no.difi.datahotel.util.model.Field;
import no.difi.datahotel.util.model.Fields;
import no.difi.datahotel.util.shared.Filesystem;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FieldsTest {

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(FieldsTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Test
	public void testSaveRead() throws Exception {

		Fields f = new Fields();
		
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("id", false));
		fields.add(new Field("name", true));
		f.setFields(fields);
		
		f.save("difi", "test", "people");
		assertTrue(Filesystem.getFile(Filesystem.FOLDER_SLAVE, "difi", "test", "people", Filesystem.FILE_FIELDS).exists());
		
		Fields f2 = Fields.read("difi/test/people");
		assertEquals(fields.size(), f2.getFields().size());
		for (int i = 0; i < fields.size(); i++)
			assertEquals(fields.get(i), f2.getFields().get(i));
			
	}
}