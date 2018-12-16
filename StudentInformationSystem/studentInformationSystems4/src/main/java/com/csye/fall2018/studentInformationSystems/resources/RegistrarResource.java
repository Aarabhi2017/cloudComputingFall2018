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
import com.csye.fall2018.studentInformationSystems.datamodels.Registrar;
import com.csye.fall2018.studentInformationSystems.service.RegistrarService;

@Path("registrar")
public class RegistrarResource {

	RegistrarService registrarService = new RegistrarService();
	
	@POST
	@Path("/addRegistrar")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Registrar addNewRegistrar(Registrar reg) {
		return registrarService.addNewRegistrar(reg);
	}
	
}
