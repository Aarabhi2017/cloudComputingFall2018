package com.csye.fall2018.studentInformationSystems.resources;

import java.util.ArrayList;
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

import com.csye.fall2018.studentInformationSystems.datamodels.Course;
import com.csye.fall2018.studentInformationSystems.service.CourseService;
import com.csye.fall2018.studentInformationSystems.service.StudentsService;

//.. /webapi/myresource
@Path("courses")
public class CourseResource {

	CourseService courseService = new CourseService();
	StudentsService studentService = new StudentsService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Course> getAllCourses() {
		return courseService.getAllCourses();
	}

	@GET
	@Path("/getRoster/{courseId}")
	// @Produces(MediaType.APPLICATION_JSON)
	public ArrayList<String> getRoster(@PathParam("courseId") String courseId) {
		return courseService.getStudentsList(courseId);
	}

	public Course getCourse(String courseId) {
		return courseService.getCourse(courseId);
	}

	@DELETE
	@Path("/delete/{courseId}")
	public boolean deleteCourse(@PathParam("courseId") String courseId) {
		return courseService.deleteCourse(courseId);
	}

	@POST
	@Path("/addCourse")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Course addNewCourse(@FormParam("courseId") String courseId, @FormParam("dept") String dept,
			@FormParam("profId") String profId) {
		return courseService.addNewCourse(courseId, dept, profId);
	}

	@POST
	@Path("/addTA/{courseId}")
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	public Course addTA(@PathParam("courseId") String courseId, @QueryParam("TAId") String studentId) {
		return courseService.addTA(courseId, studentId);
	}

	@PUT
	@Path("/changeCourseDetail/{courseId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Course changeCourseDetails(@PathParam("courseId") String courseId,
			@QueryParam("profId") String professorId) {
		return courseService.changeCourseDetails(courseId, professorId);
	}

}
