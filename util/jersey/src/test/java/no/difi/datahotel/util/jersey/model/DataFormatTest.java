package no.difi.datahotel.util.jersey.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.difi.datahotel.util.bridge.FieldLight;
import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.bridge.MetadataLight;
import no.difi.datahotel.util.jersey.CSVData;
import no.difi.datahotel.util.jersey.DataFormat;
import no.difi.datahotel.util.jersey.RequestContext;

import org.junit.Test;

import com.google.gson.Gson;

public class DataFormatTest {

	@Test
	public void testGet() throws Exception {
		assertEquals(DataFormat.TEXT_PLAIN, DataFormat.get("espen"));
		assertEquals(DataFormat.JSON, DataFormat.get("json"));
	}

	@Test
	public void testJsonFormat() {
		DataFormat df = DataFormat.JSON;

		ArrayList<String> data = new ArrayList<String>();
		data.add("Espen");
		data.add("Epson");

		assertEquals(new Gson().toJson(data), df.format(data, null));
	}

	@Test
	public void testGetters() {
		DataFormat df = DataFormat.JSON;

		assertEquals("application/json", df.getMime());
		assertEquals("json", df.getType());
	}

	@Test
	public void testError() {
		DataFormat df = DataFormat.JSON;
		String result = df.formatError("WARNING", null);

		assertTrue(result.contains("WARNING"));
		assertTrue(result.contains("error"));
	}

	@Test
	public void testFormatError() throws Exception {
		DataFormat df = DataFormat.JSON;

		Field rField = DataFormat.class.getDeclaredField("cls");
		rField.setAccessible(true);
		rField.set(df, null);

		// TODO Ta over logger.

		assertEquals("Error", df.format("Message", null));
	}

	@Test
	public void testErrorError() throws Exception {
		DataFormat df = DataFormat.JSON;

		Field rField = DataFormat.class.getDeclaredField("cls");
		rField.setAccessible(true);
		rField.set(df, null);

		// TODO Ta over logger.

		assertEquals("Error", df.formatError("Message", null));
	}

	/**
	 * Fast and simple testing of most types.
	 * 
	 * TODO Fix JSON and CSV.
	 */
	@Test
	public void testSimple() {
		List<Map<String, String>> d = new ArrayList<Map<String, String>>();
		CSVData data = new CSVData(d);
		HashMap<String, String> ds = new HashMap<String, String>();
		ds.put("description", "Values");
		ds.put("name", "Hello World!");
		d.add(ds);
		
		List<FieldLight> fields = new ArrayList<FieldLight>();
		no.difi.datahotel.util.bridge.Field field;
		field = new no.difi.datahotel.util.bridge.Field();
		field.setColumnNumber(1);
		field.setName("Name");
		field.setShortName("name");
		fields.add(field.light());
		
		field = new no.difi.datahotel.util.bridge.Field();
		field.setColumnNumber(2);
		field.setName("Description");
		field.setShortName("description");
		fields.add(field.light());

		List<MetadataLight> metadata = new ArrayList<MetadataLight>();
		Metadata mp = new Metadata();
		mp.setName("Difi");
		mp.setDescription("Agency");
		mp.setUrl("http://www.difi.no/");
		mp.setUpdated(System.currentTimeMillis());
		
		MetadataLight m = mp.light();
		metadata.add(m);
		
		Metadata meta = new Metadata();
		meta.setLocation("somewhere");
		meta.setUpdated(System.currentTimeMillis());
		
		RequestContext context = new RequestContext();
		context.setMetadata(meta);
		context.setFields(fields);
		
		for (DataFormat f : DataFormat.values()) {
			if (f != DataFormat.TEXT_PLAIN && f != DataFormat.JSON) {
				assertTrue(f.format(data, context).contains("World"));
				assertTrue(f.format(data, context).contains("escription"));

				if (f != DataFormat.CSV && f != DataFormat.CSVCORRECT) {
					assertTrue(f.format(metadata, context).contains("Difi"));
					assertTrue(f.format(metadata, context).contains("escription"));
					assertTrue(f.format(metadata, context).contains("www.difi.no"));
				}
			}
		}
	}
	
}
