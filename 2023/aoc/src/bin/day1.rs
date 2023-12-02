use std::collections::HashMap;

fn main() {
    let input = include_str!("../../input/day1.txt");
    println!("Part 1: {}", part1(input.to_string()));
    println!("Part 2: {}", part2(input.to_string()));
}

fn part1(input: String) -> u32 {
    input.lines().fold(0, |acc, line| {
        let numbers = line.chars().filter_map(|s| s.to_digit(10));

        let first = numbers.clone().next().unwrap();
        let last = numbers.rev().next().unwrap();

        acc + (first * 10 + last)
    })
}

fn part2(input: String) -> u32 {
    let text_to_number = HashMap::from([
        ("zero", 0),
        ("one", 1),
        ("two", 2),
        ("three", 3),
        ("four", 4),
        ("five", 5),
        ("six", 6),
        ("seven", 7),
        ("eight", 8),
        ("nine", 9),
        ("0", 0),
        ("1", 1),
        ("2", 2),
        ("3", 3),
        ("4", 4),
        ("5", 5),
        ("6", 6),
        ("7", 7),
        ("8", 8),
        ("9", 9),
    ]);

    input.lines().fold(0, |acc, line| {
        let first_number = text_to_number
            .iter()
            // for each string representing a number, find first occurrence in line
            .filter_map(|(key, value)| line.find(key).map(|index| (index, value)))
            // the occurrence with the smallest index is the first number
            .min_by_key(|(index, _)| *index)
            .map(|(_, value)| *value)
            .expect("Failed to find first number.");

        let last_number = text_to_number
            .iter()
            .filter_map(|(key, value)| line.rfind(key).map(|index| (index, value)))
            .max_by_key(|(index, _)| *index)
            .map(|(_, value)| *value)
            .expect("Failed to find last number.");

        acc + (first_number * 10 + last_number)
    })
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn part_one() {
        let input = "1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
";
        assert_eq!(142, part1(input.to_owned()));
    }

    #[test]
    fn part_two() {
        let input = "two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
";
        assert_eq!(281, part2(input.to_string()));
    }
}
