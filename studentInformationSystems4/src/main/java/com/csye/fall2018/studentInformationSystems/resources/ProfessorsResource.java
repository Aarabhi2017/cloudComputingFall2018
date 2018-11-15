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

import com.csye.fall2018.studentInformationSystems.datamodels.Professor;
import com.csye.fall2018.studentInformationSystems.service.ProfessorsService;

// .. /webapi/myresource
@Path("professors")
public class ProfessorsResource {

	ProfessorsService profService = new ProfessorsService();
	
	@GET
	@Path("/getByDepartment")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Professor> getProfessorsByDeparment(
			@QueryParam("department") String department			) {
		
		if (department == null) {
			return profService.getAllProfessors();
		}
		return profService.getProfessorsByDepartment(department);
		
	}
	
	@GET
	@Path("/hello")
	@Produces(MediaType.APPLICATION_JSON)
	public String getHello() {
		return "Hello-World";
	}

	// ... webapi/professor/1 
	@GET
	@Path("/{professorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Professor getProfessor(@PathParam("professorId") String profId) {
		return profService.getProfessor(profId);
	}
	
	@DELETE
	@Path("/delete/{professorId}")
	public boolean deleteProfessor(@PathParam("professorId") String profId) {
		return profService.deleteProfessor(profId);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Professor addProfessor(Professor prof) {
			return profService.addProfessor(prof);
	}
	
	@PUT
	@Path("changeDepartment/{professorId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Professor updateProfessor(@PathParam("professorId") String profId, 
			@FormParam("department") String department) {
		return profService.updateProfessorInformation(profId, department);
	}
	
//	public void addProfessor(String name, String department, Date joiningDate) {
//		profService.addProfessor(name, department, joiningDate);
//	}
 }
