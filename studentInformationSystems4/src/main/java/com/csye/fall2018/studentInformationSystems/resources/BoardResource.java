package com.csye.fall2018.studentInformationSystems.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.csye.fall2018.studentInformationSystems.datamodels.Board;
import com.csye.fall2018.studentInformationSystems.datamodels.Course;
import com.csye.fall2018.studentInformationSystems.service.BoardService;
import com.csye.fall2018.studentInformationSystems.service.CourseService;

//.. /webapi/myresource
@Path("board")
public class BoardResource {

	BoardService boardService = new BoardService();
	CourseService courseService = new CourseService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Board> getAllBoards() {
		return boardService.getAllBoards();
	}

	@GET
	@Path("/getBoard/{boardId}")
	//@Produces(MediaType.APPLICATION_JSON)
	public Board getBoard(@PathParam("boardId")String boardId) {
		return boardService.getBoard(boardId);
	}

	@DELETE
	@Path("/deleteBoard/{boardId}")
	public boolean deleteBoard(@PathParam("boardId") String boardId) {
		return boardService.deleteBoard(boardId);
	}

	@POST
	@Path("/addBoard")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Board addBoard(@FormParam("courseId") String courseId, @FormParam("boardId") String boardId)
			{
		return boardService.addNewBoard(courseId, boardId);
	}
	@PUT
	@Path("/changeBoard/{courseId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Course changeBoard(@PathParam("courseId") String courseId,
			@QueryParam("boardId") String boardId) {
		return boardService.changeBoard(courseId, boardId);
	}

}
