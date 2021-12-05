fn parse_input(input: &str) -> Vec<i32> {
    input
        .split("\n")
        .map(|line| line.parse().unwrap())
        .collect()
}

fn sum_windows(report: &[i32]) -> Vec<i32> {
    report
        .windows(3)
        .map(|window| window.iter().sum())
        .collect()
}

fn count_increases(report: &[i32]) -> usize {
    report
        .windows(2)
        .filter(|window| window[0] < window[1])
        .count()
}

pub fn solve() {
    let input = include_str!("day1.txt");
    let report = parse_input(input);
    println!("day 1A: {}", count_increases(&report));

    let report_summed_windows = sum_windows(&report);
    println!("day 1B: {}", count_increases(&report_summed_windows));
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_example() {
        let report = "199
200
208
210
200
207
240
269
260
263";
        assert_eq!(7, count_increases(&parse_input(report)));
    }
}
