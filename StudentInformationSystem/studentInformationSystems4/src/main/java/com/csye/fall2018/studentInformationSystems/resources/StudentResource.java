package com.csye.fall2018.studentInformationSystems.resources;

import java.io.ByteArrayOutputStream;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import com.csye.fall2018.studentInformationSystems.datamodels.Student;
import com.csye.fall2018.studentInformationSystems.service.CourseService;
import com.csye.fall2018.studentInformationSystems.service.StudentsService;

//.. /webapi/myresource
@Path("students")
public class StudentResource {

	StudentsService studentService = new StudentsService();
	CourseService courseService = new CourseService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getAllStudents() {
		return studentService.getAllStudents();
	}

	@GET
	@Path("/{studentId}/getCourses")
	// @Produces(MediaType.APPLICATION_JSON)
	public String getCoursesList(@PathParam("studentId") String studentId) {

		if (studentId == null) {
			return null;
		}
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(out, studentService.getRegisteredCourses(studentId));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final byte[] data = out.toByteArray();
		System.out.println(new String(data));

		return new String(data);

	}

	@POST
	@Path("/{studentId}/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public boolean registerStudentForCourse(@PathParam("studentId") String studentId,
			@FormParam("courseId") String courseId) {

		System.out.println(studentId + ":" + courseId);
		if (courseId == null) {
			return false;
		}
		return studentService.registerStudentForCourse(studentId, courseId);
	}

	@POST
	@Path("/addStudent")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Student addStudent(@FormParam("studentId") String studentId, @FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName, @FormParam("dept") String dept,
			@FormParam("emailId") String emailId) {
		return studentService.addStudent(studentId, firstName, lastName, dept, emailId);
	}

	@DELETE
	@Path("{studentId}/withdraw")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public boolean withdrawFromCourse(@PathParam("studentId") String studentId,
			@FormParam("courseId") String courseId) {

		System.out.println("Course:" + courseId + "StudID" + studentId);
		if (courseId == null) {
			return false;
		}
		return studentService.withdrawFromCourse(studentId, courseId);
	}

	@PUT
	@Path("/ModifyDept")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public boolean changeStudentDept(@FormParam("studentId") String studentId, @FormParam("deptName") String deptName) {
		return studentService.changeStudentDept(studentId, deptName);
	}
}
