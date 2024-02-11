package telran.java51.student.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import telran.java51.student.dao.StudentRepository;
import telran.java51.student.dto.ScoreDto;
import telran.java51.student.dto.StudentCreateDto;
import telran.java51.student.dto.StudentDto;
import telran.java51.student.dto.StudentUpdateDto;
import telran.java51.student.dto.exceptions.StudentNotFoundException;
import telran.java51.student.model.Student;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

	final StudentRepository studentRepository;
	final ModelMapper modelMapper;

	// @Autowired

	// @RequiredArgsConstructor и поле final это как @Autowired

	// public StudentServiceImpl(StudentRepository studentRepository) {
	// this.studentRepository = studentRepository;
	// }

	@Override
	public Boolean addStudent(StudentCreateDto studentCreateDto) {

		if (studentRepository.existsById(studentCreateDto.getId())) {
			return false;
		}
//		Student student = new Student(studentCreateDto.getId(),
//				studentCreateDto.getName(), studentCreateDto.getPassword());
		Student student = modelMapper.map(studentCreateDto, Student.class);
		studentRepository.save(student);
		return true;
	}

	@Override
	public StudentDto findStudent(Integer id) {
		Student student = studentRepository.findById(id)
				.orElseThrow(StudentNotFoundException::new);
		return modelMapper.map(student, StudentDto.class);
	}

	@Override
	public StudentDto removeStudent(Integer id) {
		Student student = studentRepository.findById(id)
				.orElseThrow(StudentNotFoundException::new);
		studentRepository.deleteById(id);
		return modelMapper.map(student, StudentDto.class);
	}

	@Override
	public StudentCreateDto updateStudent(Integer id,
			StudentUpdateDto studentUpdateDto) {

		Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
		if (studentUpdateDto.getName() != null) {
			student.setName(studentUpdateDto.getName());
		}
		if (studentUpdateDto.getPassword() != null) {
			student.setPassword(studentUpdateDto.getPassword());
		}
		student = studentRepository.save(student);
		return modelMapper.map(student, StudentCreateDto.class);
	}

	@Override
	public Boolean addScore(Integer id, ScoreDto scoreDto) {
		Student student = studentRepository.findById(id)
				.orElseThrow(StudentNotFoundException::new);
		boolean res = student.addScore(scoreDto.getExamName(), scoreDto.getScore());
		studentRepository.save(student);
		return res;
	}

	@Override
	public List<StudentDto> findStudentsByName(String name) {

		return studentRepository.findByNameIgnoreCase(name)
				.map(s -> modelMapper.map(s, StudentDto.class))
				.toList();
	}

	@Override
	public Long getStudentsNamesQuantity(Set<String> names) {
		return studentRepository.countByNameInIgnoreCase(names);
	}

	@Override
	public List<StudentDto> getStudentsByExamMinScore(String exam,
			Integer minScore) {

		return studentRepository.findByExamAndScoreGreaterThan(exam, minScore)
				.map(s -> modelMapper.map(s, StudentDto.class))
				.toList();
//		return StreamSupport
//				.stream(studentRepository.findAll().spliterator(), false)
//				.filter(s -> s.getScores().get(exam) > minScore)
//				.map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
//				.toList();
	}

}
