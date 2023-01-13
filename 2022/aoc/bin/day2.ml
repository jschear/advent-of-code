open! Core

(* TODO: figure out why Not_found is deprecated in Jane Street's stdlib *)
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
