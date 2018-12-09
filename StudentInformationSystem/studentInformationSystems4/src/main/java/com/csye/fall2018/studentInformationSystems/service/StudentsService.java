package com.csye.fall2018.studentInformationSystems.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.ListSubscriptionsResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Subscription;
import com.csye.fall2018.studentInformationSystems.datamodels.Course;
import com.csye.fall2018.studentInformationSystems.datamodels.DynamoDBConnector;
import com.csye.fall2018.studentInformationSystems.datamodels.SNSConnector;
import com.csye.fall2018.studentInformationSystems.datamodels.Student;

public class StudentsService {

	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;
	SNSConnector snsConnector;
	AmazonSNS sns;
	String tableName = "Student";

	ProfessorsService professorService = new ProfessorsService();

	public StudentsService() {
		dynamoDBConnector = new DynamoDBConnector();
		snsConnector = new SNSConnector();

		DynamoDBConnector.init();
		SNSConnector.init();

		sns = snsConnector.getClient();
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
		if (Student != null) {
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
	public Student addStudent(String studentId, String firstName, String lastName, String dept, String emailId) {
		Student s = new Student(studentId, firstName, lastName, dept, emailId);
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
	public boolean registerStudentForCourse(String studentId, String courseIdentification) {
		System.out.println("Registering course");
		CourseService courseService = new CourseService();

		Student student = getStudent(studentId);

		ArrayList<String> courseList = student.getCoursesList();
		Course course = courseService.getCourse(courseIdentification);

		ArrayList<String> roster = courseService.getStudentsList(course.getCourseId());
		roster.add(student.getStudentId());

		String topic = course.getNotificationTopic();
		if (courseList.size() >= 3) {
			System.out.println("Exceeding credits and hours!");
			return false;
		}

		courseList.add(courseIdentification);
		student.setCoursesList(courseList);
		course.setRoster(roster);
		dynamoDBMapper.save(student);
		dynamoDBMapper.save(course);

		// Creates a topic if not present, else returns existing topic
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(topic);
		CreateTopicResult createTopicResult = sns.createTopic(createTopicRequest);

		// Subscribe
		SubscribeRequest subRequest = new SubscribeRequest(createTopicResult.getTopicArn(), "email",
				student.getEmailId());
		sns.subscribe(subRequest);

		return true;
	}

	// Delete
	public boolean withdrawFromCourse(String studentId, String courseId) {
		CourseService courseService = new CourseService();

		Course course = courseService.getCourse(courseId);
		Student student = getStudent(studentId);

		ArrayList<String> courseList = student.getCoursesList();
		ArrayList<String> StudentList = courseService.getStudentsList(courseId);

		for (String courseIdentification : courseList) {
			if (courseIdentification.equalsIgnoreCase(courseId)) {

				courseList.removeAll(Arrays.asList(courseIdentification));
				student.setCoursesList(courseList);

				StudentList.removeAll(Arrays.asList(student.getStudentId()));
				course.setRoster(StudentList);

				dynamoDBMapper.save(student);
				dynamoDBMapper.save(course);

				// ListSubscriptionsResult listResult = sns.listSubscriptions();
				// List<Subscription> subscriptions = listResult.getSubscriptions();
				// String subsArn = "";
				// for (Subscription subs : subscriptions) {
				// if (subs.getProtocol().equals("email") &&
				// subs.getEndpoint().equals(student.getEmailId())) {
				// subsArn = subs.getSubscriptionArn();
				// System.out.println("Subscription ARN" + subsArn);
				// break;
				// }
				// }
				// if (!subsArn.equals("") && !subsArn.equalsIgnoreCase("PendingConfirmation"))
				// {
				// sns.unsubscribe(subsArn);
				// }

				return true;
			}
		}
		return false;

	}

}
