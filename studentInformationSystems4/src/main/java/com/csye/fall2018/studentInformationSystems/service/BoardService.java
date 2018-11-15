package com.csye.fall2018.studentInformationSystems.service;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.csye.fall2018.studentInformationSystems.datamodels.Board;
import com.csye.fall2018.studentInformationSystems.datamodels.Course;
import com.csye.fall2018.studentInformationSystems.datamodels.DynamoDBConnector;
import com.csye.fall2018.studentInformationSystems.datamodels.Professor;

public class BoardService {

	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;
	String tableName = "Board";

	CourseService courseService = new CourseService();

	public BoardService() {
		dynamoDBConnector = new DynamoDBConnector();
		DynamoDBConnector.init();
		dynamoDBMapper = new DynamoDBMapper(dynamoDBConnector.getClient());
	}

	public List<Board> getAllBoards() {
		List<Board> list = dynamoDBMapper.scan(Board.class, new DynamoDBScanExpression());
		return list;
	}

	public Board getBoard(String boardId) {
		List<Board> boardList = getAllBoards();
		for (Board board : boardList) {
			if (board.getBoardId().equalsIgnoreCase(boardId)) {
				return board;
			}
		}
		return null;
	}

	public boolean deleteBoard(String boardId) {
		// TODO Auto-generated method stub
		Board board = getBoard(boardId);
		if (board != null) {
			String c = board.getCourseId();
			Course course = courseService.getCourse(c);
			course.setBoard(null);
			dynamoDBMapper.save(course);
			dynamoDBMapper.delete(board);
			return true;
		}
		return false;
	}

	public Board addNewBoard(String courseId, String boardId) {
		// TODO Auto-generated method stub
		Course course = courseService.getCourse(courseId);
		Board board = null;
		if (course != null) {
			board = new Board(course.getCourseId(), boardId);
		}
		if (board != null) {
			course.setBoard(boardId);
			dynamoDBMapper.save(board);
			dynamoDBMapper.save(course);
			return board;
		}
		return null;
	}

	public Board getBoardForCourse(String courseId) {

		//Querybased on course
		HashMap<String, AttributeValue> expressionAttributeValue = new HashMap<String, AttributeValue>();
		expressionAttributeValue.put(":coursId", new AttributeValue().withS(courseId));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression("CourseId = :coursId")
				.withExpressionAttributeValues(expressionAttributeValue);

		List<Board> boards = dynamoDBMapper.scan(Board.class, scanExpression);
		System.out.println(boards.isEmpty());
		if(!boards.isEmpty())
		{
			return boards.get(0);
		}
		return null;
	}

	public Course changeBoard(String courseId, String boardId) {
		// TODO Auto-generated method stub
		Course course = courseService.getCourse(courseId);
		Board board = getBoardForCourse(course.getCourseId());
		System.out.println(board.getBoardId());
		if (course != null && boardId != null && board !=null) {
			board.setBoardId(boardId); 
			course.setBoard(boardId);
			dynamoDBMapper.save(course);
			dynamoDBMapper.save(board);
			return course;
		}
		return null;
	}

}
