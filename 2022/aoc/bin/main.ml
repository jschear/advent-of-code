open! Core

let rec list_max l =
  match l with [] -> Int.min_value | x :: xs -> max x (list_max xs)

let elves =
  In_channel.read_all "input/day1.txt"
  |> Str.split (Str.regexp "\n\n")
  |> List.map ~f:(Str.split (Str.regexp "\n"))
  |> List.map ~f:(List.map ~f:int_of_string)

let sums = List.map elves ~f:(List.fold ~init:0 ~f:( + ))

let () =
  let max = list_max sums in
  Printf.printf "Part 1: %d\n" max

let () =
  let sorted = List.sort sums ~compare |> List.rev in
  let sum_three_max =
    match sorted with a :: b :: c :: _ -> a + b + c | _ -> 0
  in
  Printf.printf "Part 2: %d\n" sum_three_max

(* Day 2 TODO: figure out how to move this into a different module :) *)
(* Also TODO: figure out why Not_found is deprecated in Jane Street's stdlib *)
let input = In_channel.read_all "input/day2.txt"
let rounds = Str.split (Str.regexp "\n") input

let process_line line =
  let split_line = Str.split (Str.regexp " ") line in
  match split_line with a :: b :: _ -> (a, b) | _ -> raise Caml.Not_found

let matches = List.map rounds ~f:process_line

(* A/X: Rock
   B/Y: Paper
   C/Z: Scissors *)
let score = function
  | "A", "X" -> 4
  | "A", "Y" -> 8
  | "A", "Z" -> 3
  | "B", "X" -> 1
  | "B", "Y" -> 5
  | "B", "Z" -> 9
  | "C", "X" -> 7
  | "C", "Y" -> 2
  | "C", "Z" -> 6
  | _, _ -> raise Caml.Not_found

(* X is a loss, Y is a draw, Z is a win *)
let part_2_score = function
  | "A", "X" -> 0 + 3
  | "A", "Y" -> 3 + 1
  | "A", "Z" -> 6 + 2
  | "B", "X" -> 0 + 1
  | "B", "Y" -> 3 + 2
  | "B", "Z" -> 6 + 3
  | "C", "X" -> 0 + 2
  | "C", "Y" -> 3 + 3
  | "C", "Z" -> 6 + 1
  | _, _ -> raise Caml.Not_found

let scores = List.map matches ~f:score
let part_2_scores = List.map matches ~f:part_2_score
let sum = List.fold scores ~init:0 ~f:( + )
let part_2_sum = List.fold part_2_scores ~init:0 ~f:( + )
let () = print_endline (string_of_int sum)
let () = print_endline (string_of_int part_2_sum)
