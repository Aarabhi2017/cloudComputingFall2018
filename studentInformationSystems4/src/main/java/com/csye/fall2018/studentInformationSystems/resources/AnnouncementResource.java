package com.csye.fall2018.studentInformationSystems.resources;

import java.util.List;

import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.csye.fall2018.studentInformationSystems.datamodels.Announcements;
import com.csye.fall2018.studentInformationSystems.service.AnnouncementService;
import com.csye.fall2018.studentInformationSystems.service.BoardService;

//.. /webapi/myresource
@Path("Announcements")
public class AnnouncementResource {

	BoardService boardService = new BoardService();
	AnnouncementService announcementService = new AnnouncementService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Announcements> getAllAnnouncements() {
		return announcementService.getAllAnnouncements();
	}

	@GET
	@Path("/getAnnouncements/{boardId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Announcements> getAnnouncements(@PathParam("boardId")String boardId) {
		return announcementService.getAnnouncements(boardId);
	}

	@DELETE
	@Path("/deleteAnnouncement/{boardId_announcementId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public boolean deleteAnnouncement(@PathParam("boardId_announcementId")String boardId_announcementId) {
		return announcementService.deleteAnnouncement(boardId_announcementId);
	}

	@POST
	@Path("/addAnnouncement")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Announcements addAnnouncement(@FormParam("boardId") String boardId, @FormParam("announcementId") String announcementId,
	@FormParam("text")String text)
			{
		return announcementService.addAnnouncement(boardId, announcementId,text);
	}
	@PUT
	@Path("/changeAnnouncement/{boardId_announcementId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Announcements changeAnnouncement(@PathParam("boardId_announcementId") String boardId_announcementId,
			@FormParam("text") String text) {
		return announcementService.changeAnnouncement(boardId_announcementId, text);
	}

}