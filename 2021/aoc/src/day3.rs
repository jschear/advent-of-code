use std::cmp::Ordering;

// TODO: there's got to be a better representation
fn parse_input(input: &str) -> Vec<Vec<u32>> {
    input
        .split("\n")
        .map(|line: &str| {
            line.chars()
                .map(|c: char| -> u32 { c.to_digit(10).expect("Invalid character") })
                .collect()
        })
        .collect()
}

fn gamma_epsilon_product(report: &Vec<Vec<u32>>) -> u32 {
    let report_length = report.len();
    let half = (report_length + 1) / 2;
    let number_length = report.first().expect("Empty vector").len();
    let zero = vec![0; number_length];
    let sum = report.iter().fold(zero, |acc, line| sum(&acc, line));

    let gamma: Vec<u32> = sum
        .iter()
        .map(|&item| {
            if usize::try_from(item).unwrap() >= half {
                1
            } else {
                0
            }
        })
        .collect();
    let epsilon: Vec<u32> = gamma
        .iter()
        .map(|&bit| match bit {
            0 => 1,
            1 => 0,
            _ => panic!("Unexpected bit"),
        })
        .collect();

    let gamma_decimal = to_u32(&gamma);
    let epsilon_decimal = to_u32(&epsilon);
    gamma_decimal * epsilon_decimal
}

fn to_u32(binary: &[u32]) -> u32 {
    binary.iter().fold(0, |acc, &b| acc * 2 + b)
}

fn sum(a: &[u32], b: &[u32]) -> Vec<u32> {
    a.iter()
        .zip(b.iter())
        .map(|(aitem, bitem)| aitem + bitem)
        .collect()
}

fn life_support_rating(report: &Vec<Vec<u32>>) -> u32 {
    let co2 = find_match(report, |digit, most_common| digit == most_common);
    let oxygen = find_match(report, |digit, most_common| digit != most_common);

    let co2_decimal = to_u32(&co2);
    let oxygen_decimal = to_u32(&oxygen);
    co2_decimal * oxygen_decimal
}

fn find_match<F>(numbers: &Vec<Vec<u32>>, f: F) -> Vec<u32>
where
    F: Fn(u32, u32) -> bool,
{
    let digit_count = numbers.first().expect("Empty vector").len();
    let mut numbers = numbers.clone();
    for i in 0..digit_count {
        let most_common = most_common(&numbers, i);
        numbers.retain(|number| f(number[i], most_common));
        if numbers.len() == 1 {
            break;
        }
    }
    numbers.first().expect("No number found").to_vec()
}

fn most_common(numbers: &Vec<Vec<u32>>, index: usize) -> u32 {
    let count = numbers.len();
    let half: u32 = ((count + 1) / 2).try_into().unwrap();
    let sum = numbers.iter().fold(0, |acc, number| acc + number[index]);
    match sum.cmp(&half) {
        Ordering::Less => 0,
        Ordering::Greater => 1,
        Ordering::Equal => 1,
    }
}

pub fn solve() {
    let input = include_str!("day3.txt");
    let report = parse_input(input);
    println!("day 3A: {}", gamma_epsilon_product(&report));
    println!("day 3B: {}", life_support_rating(&report));
}

#[cfg(test)]
mod tests {
    use super::*;

    static INPUT: &str = "00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010";

    #[test]
    fn test_example() {
        let report = parse_input(INPUT);
        assert_eq!(198, gamma_epsilon_product(&report));
    }

    #[test]
    fn test_example_part_two() {
        let report = parse_input(INPUT);
        assert_eq!(230, life_support_rating(&report));
    }
}
