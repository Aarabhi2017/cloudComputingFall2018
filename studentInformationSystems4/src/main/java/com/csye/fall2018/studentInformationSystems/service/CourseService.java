package com.csye.fall2018.studentInformationSystems.service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.csye.fall2018.studentInformationSystems.datamodels.Board;
import com.csye.fall2018.studentInformationSystems.datamodels.Course;
import com.csye.fall2018.studentInformationSystems.datamodels.DynamoDBConnector;
import com.csye.fall2018.studentInformationSystems.datamodels.Professor;
import com.csye.fall2018.studentInformationSystems.datamodels.Student;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CourseService {

	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;
	String tableName = "Course";

	ProfessorsService professorService = new ProfessorsService();
	StudentsService studentService = new StudentsService();

	public CourseService() {
		dynamoDBConnector = new DynamoDBConnector();
		DynamoDBConnector.init();
		dynamoDBMapper = new DynamoDBMapper(dynamoDBConnector.getClient());
	}

	// GET methods
	public List<Course> getAllCourses() {
		List<Course> list = dynamoDBMapper.scan(Course.class, new DynamoDBScanExpression());
		return list;
	}

	public Course getCourse(String courseId) {
		HashMap<String, String> expressionAttributesNames = new HashMap<>();
		expressionAttributesNames.put("#courseId", "courseId");

		HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":courseId", new AttributeValue().withS(courseId));

		DynamoDBQueryExpression<Course> dynamoDBQueryExpression = new DynamoDBQueryExpression<Course>()
				.withIndexName("courseId-index").withKeyConditionExpression("#courseId = :courseId")
				.withExpressionAttributeNames(expressionAttributesNames)
				.withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);

		List<Course> Course = dynamoDBMapper.query(Course.class, dynamoDBQueryExpression);
		if(Course!=null)
		{
			return Course.get(0);
		}
		return null;
	}

	// POST methods
	public Course addNewCourse(String courseId,String dept, String professorId) {

		Professor prof = professorService.getProfessor(professorId);
		if(prof==null)
		{
			return null;
		}
		Course course = new Course(courseId,dept,professorId);
//		course.setProfessor(prof.getProfessorId());
//		course.setCourseId(courseId);
//		course.setDepartment(dept);
		
		dynamoDBMapper.save(course);

		return course;
	}
	
	public String getStudentsList(String courseId)
	{
		Course course = getCourse(courseId);
		if(course!=null)
		{
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final ObjectMapper mapper = new ObjectMapper();

			try {
				mapper.writeValue(out,course.getRoster());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final byte[] data = out.toByteArray();
			System.out.println("ROSTER - "+new String(data));
			return new String(data);
		}
		return null;
	}

	public Course addTA(String courseId, String studentId) {

		StudentsService StudentService = new StudentsService();
		Student student = StudentService.getStudent(studentId);

		Course course = getCourse(courseId);
		if (course != null && student != null) {
			course.setTA(student.getStudentId());

			dynamoDBMapper.save(course);
			return getCourse(courseId);
		}
		return null;
	}

	// Delete
	public boolean deleteCourse(String courseId) {
		Course course = getCourse(courseId);

		if (course != null) {
			dynamoDBMapper.delete(course);
			return true;
		}
		return false;
	}

	// Put
	public Course changeCourseDetails(String courseId, String newProfId) {
		if (courseId == null)
			return null;
		Course course = getCourse(courseId);
		if (course != null) {
			course.setProfessor(newProfId);
		}
		dynamoDBMapper.save(course);
		return course;
	}

	//Get
	public String getBoardDetails(String courseId) {
		if(courseId!=null)
		{
			Course c = getCourse(courseId);
			return c.getBoard();
		}
		return null;
	}
}
