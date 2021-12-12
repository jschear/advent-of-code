fn num_fish_after(input: &str, days: u32) -> usize {
    let initial_fish = input
        .split(",")
        .map(|s| s.parse().unwrap())
        .collect::<Vec<usize>>();

    let mut fish_counts = [0; 9];
    for fish in initial_fish {
        fish_counts[fish] += 1
    }
    for _ in 0..days {
        fish_counts.rotate_left(1);
        fish_counts[6] += fish_counts[8]
    }
    fish_counts.iter().sum()
}

pub fn solve() {
    let input = include_str!("day6.txt");
    println!("day 6A: {}", num_fish_after(input, 80));
    println!("day 6B: {}", num_fish_after(input, 256));
}

#[cfg(test)]
mod tests {
    use super::*;

    static INPUT: &str = "3,4,3,1,2";

    #[test]
    fn test_example() {
        assert_eq!(5934, num_fish_after(INPUT, 80));
    }

    #[test]
    fn test_example_part_two() {
        assert_eq!(26984457539, num_fish_after(INPUT, 256))
    }
}
