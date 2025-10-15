-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 15, 2025 at 07:15 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `packpal_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `item_id` int(11) NOT NULL,
  `list_id` int(11) NOT NULL,
  `item_name` varchar(255) NOT NULL,
  `category` varchar(50) DEFAULT 'Other',
  `packed` tinyint(1) DEFAULT 0,
  `priority` int(11) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`item_id`, `list_id`, `item_name`, `category`, `packed`, `priority`, `created_at`) VALUES
(1, 2, 'shirt', 'Clothing', 1, 0, '2025-10-13 21:21:12'),
(3, 2, 'Sunscreen', 'Toiletries', 1, 0, '2025-10-13 21:21:37'),
(4, 2, 'short', 'Clothing', 1, 0, '2025-10-13 21:22:13'),
(5, 2, 'Soap', 'Toiletries', 1, 0, '2025-10-14 16:25:02');

-- --------------------------------------------------------

--
-- Table structure for table `packing_lists`
--

CREATE TABLE `packing_lists` (
  `list_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `list_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `destination` varchar(100) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `trip_type` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `packing_lists`
--

INSERT INTO `packing_lists` (`list_id`, `user_id`, `list_name`, `description`, `destination`, `start_date`, `end_date`, `trip_type`, `created_at`) VALUES
(1, 1, 'Weekend Getaway', 'A short relaxing trip', 'Mombasa', '2025-11-01', '2025-11-03', 'Weekend', '2025-10-13 20:03:05'),
(2, 2, 'Weekend Getaway', 'vacation', 'Mombasa', '2025-10-13', '2025-10-17', 'Weekend', '2025-10-13 20:58:51');

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `token_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `expires_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `templates`
--

CREATE TABLE `templates` (
  `template_id` int(11) NOT NULL,
  `template_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `trip_type` varchar(50) DEFAULT NULL,
  `is_default` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `templates`
--

INSERT INTO `templates` (`template_id`, `template_name`, `description`, `trip_type`, `is_default`, `created_at`) VALUES
(1, 'Weekend Getaway', 'Essential items for a short weekend trip', 'weekend', 1, '2025-10-12 16:14:49'),
(2, 'Business Trip Template', 'Professional items for business travel', 'business', 1, '2025-10-12 16:14:49'),
(3, 'Beach Vacation', 'Everything you need for a beach holiday', 'beach', 1, '2025-10-12 16:14:49'),
(4, 'Camping Essentials', 'Outdoor camping gear and supplies', 'camping', 1, '2025-10-12 16:14:49');

-- --------------------------------------------------------

--
-- Table structure for table `template_items`
--

CREATE TABLE `template_items` (
  `template_item_id` int(11) NOT NULL,
  `template_id` int(11) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `priority` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `template_items`
--

INSERT INTO `template_items` (`template_item_id`, `template_id`, `item_name`, `category`, `priority`) VALUES
(1, 1, 'Passport', 'Documents', 1),
(2, 1, 'Phone charger', 'Electronics', 2),
(3, 1, 'Toothbrush', 'Toiletries', 2),
(4, 1, 'Sunglasses', 'Accessories', 3),
(5, 2, 'Laptop', 'Electronics', 1),
(6, 2, 'Business cards', 'Documents', 1),
(7, 2, 'Professional attire', 'Clothing', 2),
(8, 2, 'Presentation materials', 'Documents', 2);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `email`, `password_hash`, `created_at`) VALUES
(1, 'Test User', 'test@example.com', 'password123', '2025-10-13 20:03:05'),
(2, 'Max', 'max@gmail.com', '521339330992771ff581147d86f9b54542f66adbace64254fab67e0059c44075', '2025-10-13 20:56:50');

-- --------------------------------------------------------

--
-- Table structure for table `user_settings`
--

CREATE TABLE `user_settings` (
  `setting_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `notifications_enabled` tinyint(1) DEFAULT 1,
  `default_template` varchar(50) DEFAULT NULL,
  `theme` varchar(20) DEFAULT 'light'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `list_id` (`list_id`);

--
-- Indexes for table `packing_lists`
--
ALTER TABLE `packing_lists`
  ADD PRIMARY KEY (`list_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`token_id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `idx_reset_token` (`token`),
  ADD KEY `idx_reset_expires` (`expires_at`);

--
-- Indexes for table `templates`
--
ALTER TABLE `templates`
  ADD PRIMARY KEY (`template_id`);

--
-- Indexes for table `template_items`
--
ALTER TABLE `template_items`
  ADD PRIMARY KEY (`template_item_id`),
  ADD KEY `template_id` (`template_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_settings`
--
ALTER TABLE `user_settings`
  ADD PRIMARY KEY (`setting_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `packing_lists`
--
ALTER TABLE `packing_lists`
  MODIFY `list_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  MODIFY `token_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `templates`
--
ALTER TABLE `templates`
  MODIFY `template_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `template_items`
--
ALTER TABLE `template_items`
  MODIFY `template_item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `user_settings`
--
ALTER TABLE `user_settings`
  MODIFY `setting_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `items`
--
ALTER TABLE `items`
  ADD CONSTRAINT `items_ibfk_1` FOREIGN KEY (`list_id`) REFERENCES `packing_lists` (`list_id`) ON DELETE CASCADE;

--
-- Constraints for table `packing_lists`
--
ALTER TABLE `packing_lists`
  ADD CONSTRAINT `packing_lists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD CONSTRAINT `password_reset_tokens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `template_items`
--
ALTER TABLE `template_items`
  ADD CONSTRAINT `template_items_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `templates` (`template_id`) ON DELETE CASCADE;

--
-- Constraints for table `user_settings`
--
ALTER TABLE `user_settings`
  ADD CONSTRAINT `user_settings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
