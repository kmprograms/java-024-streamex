package pl.kmprograms;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App
{
    public static void main( String[] args )
    {
        // Pelna kompatybilnosc z Java 8
        // https://github.com/amaembo/streamex

        List<Person> people = List.of(
                new Person("ANDY", 10),
                new Person("TIM", 50),
                new Person("JIM", 10),
                new Person("ROB", 40),
                new Person("JOHN", 20),
                new Person("JIM", 30),
                new Person("CAROLINE", 60)
        );

        // skrocenie metod Collectors
        var people2 = StreamEx
                .of(people)
                .map(Person::name)
                .toList();
        System.out.println(people2);

        var countedByAge = StreamEx
                .of(people)
                .groupingBy(Person::age, Collectors.counting());
        System.out.println(countedByAge);

        var m = StreamEx.of(people)
                .filterBy(Person::age, 10)
                .append(new Person("CHARLES", 10))
                .append(new Person("EVE", 10))
                .append(new Person("PATRICK", 10))
                .mapFirst(worker -> new Person(worker.name().toLowerCase(), worker.age()))
                .mapLast(worker -> new Person(worker.name().substring(0, 1).toUpperCase() + worker.name().substring(1).toLowerCase(), worker.age()))
                .mapToEntry(value -> Map.entry(value.name(), value.age()))
                .toMap();
        System.out.println(m);

        // collapse - zastepuje serie elementow ktore pasuja do predykatu
        // pierwszym elementem z tej serii

        // porownanie odbywa sie parami, wiec jezeli dla danej pary
        // nie wystapi warunek to koniec serii
        // dla ponizszego przykladu mamy serie:
        // 1, 0, 0          -> 1
        // 8, 7, 7, 5, 3    -> 8
        // 4, 2, 1          -> 4
        // 7                -> 7
        var numbers1 = StreamEx
                .of(1, 0, 0, 8, 7, 7, 5, 3, 4, 2, 1, 7)
                .collapse((v1, v2) -> v1 >= v2)
                .toList();
        System.out.println(numbers1);

        // w tym przypadku zwraca element dominujacy sposrod do tej pory przejrzanych
        // u nas bierze  1, 8 oraz 9 bo nie wystapily przed nimi elementy wieksze
        List<Integer> numbers2 = StreamEx
                .of(1, 0, 0, 8, 7, 7, 5, 3, 4, 9, 1, 7)
                .collect(MoreCollectors.dominators((v1, v2) -> v1 >= v2));
        System.out.println(numbers2);


        // 2. Latwe manipulowanie tablicami
        var a1 = new int[]{10, 20, 30};
        var a2 = IntStreamEx.of(a1).append(40).append(50).toArray();
        System.out.println(Arrays.toString(a1));
        System.out.println(Arrays.toString(a2));

        // 3. Latwe manipulowanie mapami
        Map<Person, List<String>> peopleWithHobbies = Map.of(
                new Person("ANDY", 10), List.of("SPORT", "MUSIC"),
                new Person("LUCY", 20), List.of("MOVIES", "BOOKS"),
                new Person("JIM", 30), List.of("MOVIES", "BOOKS"),
                new Person("TIM", 40), List.of("SPORT", "MUSIC")
        );

        Map<String, List<Person>> mm = EntryStream
                .of(peopleWithHobbies)
                .flatMapValues(values -> StreamEx.of(values).map(String::toLowerCase))
                .invert() // zamiana kluczy z wartosciami
                .grouping();
        System.out.println(mm);

        var a3 = IntStreamEx.of(10, 20, 30, 11, 32, 67, 32).pairMap(Integer::max).toArray();
        System.out.println(Arrays.toString(a3));

    }
}
