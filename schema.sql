-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 08, 2026 at 05:57 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `schema`
--

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `id` bigint(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `type` enum('INCOME','EXPENSE') NOT NULL,
  `category` varchar(50) NOT NULL,
  `transaction_date` date NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`id`, `description`, `amount`, `type`, `category`, `transaction_date`, `user_id`, `created_at`) VALUES
(1, 'mercado', 500.00, 'EXPENSE', 'Alimentação', '2026-04-08', 1, '2026-04-08 05:29:52'),
(2, 'salario', 3000.00, 'INCOME', 'Salário', '2026-04-08', 1, '2026-04-08 05:30:45'),
(3, 'aluguel', 300.00, 'EXPENSE', 'Dívidas', '2026-04-08', 1, '2026-04-08 05:31:17'),
(4, 'salario 2', 4000.00, 'INCOME', 'Salário', '2026-05-07', 1, '2026-04-08 05:32:13'),
(5, 'carro', 500.00, 'EXPENSE', 'Transporte', '2026-05-08', 1, '2026-04-08 05:33:03'),
(6, 'videogame', 1000.00, 'EXPENSE', 'Lazer', '2026-05-08', 1, '2026-04-08 05:36:18'),
(7, 'carro', 2000.00, 'EXPENSE', 'Transporte', '2026-05-08', 1, '2026-04-08 05:36:59'),
(8, 'salario', 2000.00, 'INCOME', 'Salário', '2025-03-03', 1, '2026-04-08 06:51:46'),
(9, 'aluguel', 200.00, 'EXPENSE', 'Dívidas', '2025-03-05', 1, '2026-04-08 06:52:23'),
(10, 'mercado', 300.00, 'EXPENSE', 'Alimentação', '2025-03-19', 1, '2026-04-08 06:52:45');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `created_at`) VALUES
(1, 'Bernardo Sinhorelli', 'bernardosinhorelli348@gmail.com', '123456', '2026-04-08 05:23:55');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
