package com.csye.fall2018.studentInformationSystems.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.Topic;
import com.csye.fall2018.studentInformationSystems.datamodels.Course;
import com.csye.fall2018.studentInformationSystems.datamodels.DynamoDBConnector;
import com.csye.fall2018.studentInformationSystems.datamodels.Professor;
import com.csye.fall2018.studentInformationSystems.datamodels.SNSConnector;
import com.csye.fall2018.studentInformationSystems.datamodels.Student;

public class CourseService {

	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;
	SNSConnector snsConnector;
	AmazonSNS sns;
	String tableName = "Course";

	ProfessorsService professorService = new ProfessorsService();
	StudentsService studentService = new StudentsService();

	public CourseService() {
		dynamoDBConnector = new DynamoDBConnector();
		snsConnector = new SNSConnector();

		DynamoDBConnector.init();
		SNSConnector.init();

		sns = snsConnector.getClient();
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
		if (Course != null) {
			return Course.get(0);
		}
		return null;
	}

	// POST methods
	public Course addNewCourse(String courseId, String dept, String professorId) {

		Professor prof = professorService.getProfessor(professorId);
		if (prof == null) {
			return null;
		}
		Course course = new Course(courseId, dept, professorId, " ");

		// Creates a topic
		// CreateTopicRequest createTopicRequest = new CreateTopicRequest(courseId);
		// CreateTopicResult createTopicResult = sns.createTopic(createTopicRequest);
		//
		// System.out.println("Value" + createTopicResult.toString());

		dynamoDBMapper.save(course);

		return course;
	}

	public List<String> getStudentsList(String courseId) {
		Course c = getCourse(courseId);
		System.out.println("Roster" + c.getRoster());
		return c.getRoster();
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
		String topicArn = "";

		if (course != null) {
			dynamoDBMapper.delete(course);

			List<Topic> topics = sns.listTopics().getTopics();
			for (Topic topic : topics) {
				if (topic.toString().contains(course.getNotificationTopic())) {
					topicArn = topic.getTopicArn();
					System.out.println(topic.toString());
					break;
				}
			}
			if (!topicArn.isEmpty()) {
				sns.deleteTopic(topicArn);
			}
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

	// Get
	public String getBoardDetails(String courseId) {
		if (courseId != null) {
			Course c = getCourse(courseId);
			return c.getBoard();
		}
		return null;
	}
}
