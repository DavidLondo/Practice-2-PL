import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Main {

    static List<Student> students;

    public static void main(String[] args) throws IOException {
        UploadCSV();
        System.out.println();
        StudentsPerCareer();
        System.out.println();
        HigherScore();
        System.out.println();
        HigherScorePerC();
        System.out.println();
        PromScoreCareer();
        System.out.println();
        CountFemalesPerCareer();
        System.out.println();
        CountMalesPerCareer();
    }

    static void UploadCSV() throws IOException{
        Pattern pattern =Pattern.compile(",");
        String filename= "student-scores.csv";

        try(Stream<String> lines = Files.lines(Path.of(filename))){
            students=lines.skip(1).map(line->{
                String[]arr=pattern.split(line);
                return new Student(Integer.parseInt(arr[0]),arr[1],arr[2],arr[4],arr[9],Integer.parseInt(arr[10]));
            }).collect(Collectors.toList());
            students.forEach(System.out::println);
        }
    }

    static void StudentsPerCareer(){
        System.out.printf("%nConteo de estudiantes por carrera:%n");
        Map<String, Long> studentsincareer =
                students.stream()
                        .collect(Collectors.groupingBy(Student::getCareer_aspiration,
                                TreeMap::new, Collectors.counting()));
        studentsincareer.forEach(
                (departamento, conteo) -> System.out.printf(
                        "%s tiene %d estudiantes(s)%n", departamento, conteo));
    }

    static void HigherScore(){
        System.out.println("\nEstudiantes con el mejor puntaje: ");
        Function<Student, Integer> Score = Student::getMath_score;
        Comparator<Student> DScore =
                Comparator.comparing(Score);
        Map<String, List<Student>> group =
                students.stream().filter(a-> a.getMath_score() == 100).collect(Collectors.groupingBy(Student::getCareer_aspiration));
        group.forEach(
                (carrera, estudiantesenCarrera) ->
                {
                    Student HStudent= estudiantesenCarrera.stream().sorted(DScore).findFirst().get();
                    System.out.print(
                            HStudent.getFirst_name() + " " +
                            HStudent.getLast_name() + ": " +
                            HStudent.getMath_score());
                    System.out.println();
                }
            );

    }

    static void HigherScorePerC(){
        Function<Student, Integer> porSalario = Student::getMath_score;
        Comparator<Student> SalarioDescendete =
                Comparator.comparing(porSalario);
        System.out.printf("%nEstudiantes con mejores notas por carrera: %n");
        Map<String, List<Student>> agrupadoPorDepartamento =
                students.stream()
                        .collect(Collectors.groupingBy(Student::getCareer_aspiration));
        agrupadoPorDepartamento.forEach(
                (departamento, empleadosEnDepartamento) ->
                {
                    System.out.print(departamento+": ");
                    Student empleadoMas= empleadosEnDepartamento.stream().max(SalarioDescendete).get();
                    System.out.println(empleadoMas.getFirst_name()+" "+empleadoMas.getLast_name()+" "+empleadoMas.getMath_score());
                }
        );
    }

    static void PromScoreCareer(){
        Map<String, List<Student>> agrupadoPorDepartamento =
                students.stream()
                        .collect(Collectors.groupingBy(Student::getCareer_aspiration));
        System.out.println("\nPromedio de puntaje de los estudiantes por carrera: ");
        agrupadoPorDepartamento.forEach((departamento, empleadosporDepa)-> {
            System.out.print(departamento+": ");
            System.out.printf("%.2f", empleadosporDepa
                    .stream()
                    .mapToDouble(Student::getMath_score)
                    .average()
                    .getAsDouble());
            System.out.println();
        });


    }

    static void CountFemalesPerCareer() {
        System.out.printf("%nConteo de mujeres por carrera:%n");
        Map<String, Long> femalesInCareer =
                students.stream()
                        .filter(student -> "female".equals(student.getGender()))
                        .collect(Collectors.groupingBy(
                                Student::getCareer_aspiration,
                                Collectors.counting())
                        );
        femalesInCareer.forEach(
                (carrera, count) -> System.out.printf(
                        "%s tiene %d mujeres%n", carrera, count)
        );
    }

    static void CountMalesPerCareer() {
        System.out.printf("%nConteo de hombres por carrera:%n");
        Map<String, Long> malesInCareer =
                students.stream()
                        .filter(student -> "male".equals(student.getGender()))
                        .collect(Collectors.groupingBy(
                                Student::getCareer_aspiration,
                                Collectors.counting())
                        );
        malesInCareer.forEach(
                (carrera, count) -> System.out.printf(
                        "%s tiene %d hombres%n", carrera, count)
        );
    }

}