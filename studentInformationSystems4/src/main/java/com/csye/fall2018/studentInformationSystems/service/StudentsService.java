package com.csye.fall2018.studentInformationSystems.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.csye.fall2018.studentInformationSystems.datamodels.Course;
import com.csye.fall2018.studentInformationSystems.datamodels.DynamoDBConnector;
import com.csye.fall2018.studentInformationSystems.datamodels.Student;

public class StudentsService {

	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;
	String tableName = "Student";

	ProfessorsService professorService = new ProfessorsService();

	public StudentsService() {
		dynamoDBConnector = new DynamoDBConnector();
		DynamoDBConnector.init();
		dynamoDBMapper = new DynamoDBMapper(dynamoDBConnector.getClient());
	}

	// Getting a list of all students
	// GET "..webapi/programs"
	public List<Student> getAllStudents() {
		List<Student> studentList = dynamoDBMapper.scan(Student.class, new DynamoDBScanExpression());
		return studentList;

	}

	public Student getStudent(String studentId) {
		// Getting the list
		System.out.println(studentId);
		HashMap<String, String> expressionAttributesNames = new HashMap<>();
		expressionAttributesNames.put("#studentId", "studentId");

		HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":studentId", new AttributeValue().withS(studentId));

		DynamoDBQueryExpression<Student> dynamoDBQueryExpression = new DynamoDBQueryExpression<Student>()
				.withIndexName("studentId-index").withKeyConditionExpression("#studentId = :studentId")
				.withExpressionAttributeNames(expressionAttributesNames)
				.withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);

		List<Student> Student = dynamoDBMapper.query(Student.class, dynamoDBQueryExpression);
		if(Student!=null)
		{
			return Student.get(0);
		}
		return null;
	}

	public List<String> getRegisteredCourses(String studentId) {
		Student student = getStudent(studentId);
		System.out.println(student.getCoursesList());
		return student.getCoursesList();
	}

	// Adding students - POST
	public Student addStudent(String studentId, String firstName, String lastName, String dept) {
		Student s = new Student(studentId, firstName, lastName, dept);
		dynamoDBMapper.save(s);
		return s;

	}

	// Modify - PUT
	public boolean changeStudentDept(String studentId, String dept) {
		Student student = getStudent(studentId);
		student.setDept(dept);
		student.setCoursesList(new ArrayList<String>());
		dynamoDBMapper.save(student);
		return true;

	}

	// POST
	public boolean addCourse(String studentId, String courseIdentification) {

		CourseService courseService = new CourseService();
		Student student = getStudent(studentId);
		ArrayList<String> roster = new ArrayList<String>();
		roster.add(student.getStudentId());

		ArrayList<String> courseList = student.getCoursesList();
		Course course = courseService.getCourse(courseIdentification);

		if (course != null) {
			courseList.add(courseIdentification);
			student.setCoursesList(courseList);
			course.setRoster(roster);
			dynamoDBMapper.save(student);
			dynamoDBMapper.save(course);
			return true;
		}
		return false;
	}

	// Delete
	public boolean withdrawFromCourse(String studentId, String courseId) {
		Student student = getStudent(studentId);
		ArrayList<String> courseList = student.getCoursesList();

		for (String courseIdentification : courseList) {
			if (courseIdentification.equalsIgnoreCase(courseId)) {
				courseList.removeAll(Arrays.asList(courseIdentification));
				student.setCoursesList(courseList);
				dynamoDBMapper.save(student);
				return true;
			}
		}
		return false;

	}

}
