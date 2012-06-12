package no.difi.datahotel.slave.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.slave.logic.ChunkEJB;
import no.difi.datahotel.slave.logic.DataEJB;
import no.difi.datahotel.util.jersey.DataFormat;
import no.difi.datahotel.util.jersey.RequestContext;
import no.difi.datahotel.util.model.Metadata;
import no.difi.datahotel.util.shared.DatahotelException;

@Path("/download/")
@Stateless
public class DownloadService extends BaseService {

	Logger logger = Logger.getLogger(DownloadService.class.getSimpleName());

	@EJB
	private DataEJB dataEJB;
	@EJB
	private ChunkEJB chunkEJB;

	@GET
	@Path("{location: [a-z0-9\\-/]*}")
	public Response getFullDataset(@PathParam("location") String location, @Context HttpServletRequest req,
			@Context UriInfo uriInfo) {
		DataFormat dataFormat = DataFormat.CSVCORRECT;
		try {
			Metadata metadata = dataEJB.getChild(location);
			if (metadata == null)
				return returnNotFound("Dataset not found or not ready.");

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			return Response.ok(chunkEJB.getFullDataset(metadata)).type(dataFormat.getMime())
					.header("ETag", metadata.getUpdated()).build();
		} catch (DatahotelException e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), null)).type(dataFormat.getMime()).status(404)
					.build();
		}
	}
	
	@GET
	@Path("{location: [a-z0-9\\-/]*}/meta.xml")
	public Response getMetadata(@PathParam("location") String location, @Context HttpServletRequest req,
			@Context UriInfo uriInfo) {
		DataFormat dataFormat = DataFormat.XML;
		try {
			Metadata metadata = dataEJB.getChild(location);
			if (metadata == null)
				return returnNotFound("Dataset not found or not ready.");

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			return Response.ok(chunkEJB.getMetadata(metadata)).type(dataFormat.getMime())
					.header("ETag", metadata.getUpdated()).build();
		} catch (DatahotelException e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), null)).type(dataFormat.getMime()).status(404)
					.build();
		}
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/fields.xml")
	public Response getFields(@PathParam("location") String location, @Context HttpServletRequest req,
			@Context UriInfo uriInfo) {
		DataFormat dataFormat = DataFormat.XML;
		try {
			Metadata metadata = dataEJB.getChild(location);
			if (metadata == null)
				return returnNotFound("Dataset not found or not ready.");

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			return Response.ok(chunkEJB.getFields(metadata)).type(dataFormat.getMime())
					.header("ETag", metadata.getUpdated()).build();
		} catch (DatahotelException e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), null)).type(dataFormat.getMime()).status(404)
					.build();
		}
	}
}
