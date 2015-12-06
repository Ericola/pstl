-- phpMyAdmin SQL Dump
-- version 4.2.10
-- http://www.phpmyadmin.net
 --
-- Host: localhost:3306
-- Generation Time: Apr 12, 2015 at 03:18 PM
-- Server version: 5.5.38
-- PHP Version: 5.6.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `pstl`
--

-- --------------------------------------------------------

--
-- Table structure for table `can_speak`
--

DROP TABLE IF EXISTS `can_speak`;
CREATE TABLE `can_speak` (
  `user_id` int(11) NOT NULL,
  `language_id` int(11) NOT NULL,
  `level` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `competence`
--

DROP TABLE IF EXISTS `competence`;
CREATE TABLE `competence` (
`id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `competence`
--

INSERT INTO `competence` (`id`, `name`) VALUES
(1, 'java'),
(2, 'android'),
(3, 'ios');

-- --------------------------------------------------------

--
-- Table structure for table `experience`
--

DROP TABLE IF EXISTS `experience`;
CREATE TABLE `experience` (
`id` int(11) NOT NULL,
  `title` varchar(50) NOT NULL,
  `company` varchar(50) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `description` varchar(1000) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `formation`
--

DROP TABLE IF EXISTS `formation`;
CREATE TABLE `formation` (
`id` int(11) NOT NULL,
  `diploma` varchar(100) NOT NULL,
  `school` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
CREATE TABLE `language` (
`id` int(11) NOT NULL,
  `name` varchar(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `odf`
--

DROP TABLE IF EXISTS `odf`;
CREATE TABLE `odf` (
  `word` varchar(25) NOT NULL,
  `df` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `otf`
--

DROP TABLE IF EXISTS `otf`;
CREATE TABLE `otf` (
  `word` varchar(25) NOT NULL,
  `offer` varchar(25) NOT NULL,
  `tf` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
CREATE TABLE `session` (
`id` int(11) NOT NULL,
  `token` varchar(32) NOT NULL,
  `start` bigint(20) NOT NULL,
  `remember_me` tinyint(4) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `session`
--

INSERT INTO `session` (`id`, `token`, `start`, `remember_me`, `user_id`) VALUES
(58, '5g7yf31dpdfnsmdc1sfa1sv0ro1usdnp', 1428317196055, 0, 15),
(59, '5qufcx0cc0tbdeyrv0oqyh50h3q54cqr', 1428347208523, 0, 15),
(61, 'hr32tjh3uf8rmcyuoh1ate2xz9284q64', 1428661032789, 0, 15),
(62, 'akjqljiv1cwgqcgasf0av4e8ld3l270h', 1428834591831, 0, 15);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
`id` int(11) NOT NULL,
  `username` varchar(25) NOT NULL,
  `password` varchar(32) NOT NULL,
  `last_name` varchar(25) DEFAULT NULL,
  `first_name` varchar(25) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `type` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`, `password`, `last_name`, `first_name`, `email`, `type`) VALUES
(15, 'vincent', 'b15ab3f829f0f897fe507ef548741afb', 'Cordobes', 'Vincent', 'vincent.cordobes@upmc.fr', 0),
(16, 'eric', '29988429c481f219b8c5ba8c071440e1', 'Lim', 'Eric', 'eric.lim@upmc.fr', 1);

-- --------------------------------------------------------

--
-- Table structure for table `user_competence`
--

DROP TABLE IF EXISTS `user_competence`;
CREATE TABLE `user_competence` (
  `user_id` int(11) NOT NULL,
  `competence_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_competence`
--

INSERT INTO `user_competence` (`user_id`, `competence_id`) VALUES
(15, 1),
(15, 2),
(15, 3);

-- --------------------------------------------------------

--
-- Table structure for table `user_favored_competence`
--

DROP TABLE IF EXISTS `user_favored_competence`;
CREATE TABLE `user_favored_competence` (
  `user_id` int(11) NOT NULL,
  `competence_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_favored_competence`
--

INSERT INTO `user_favored_competence` (`user_id`, `competence_id`) VALUES
(15, 1),
(15, 2),
(15, 3);

-- --------------------------------------------------------

--
-- Table structure for table `user_formation`
--

DROP TABLE IF EXISTS `user_formation`;
CREATE TABLE `user_formation` (
  `user_id` int(11) NOT NULL,
  `formation_id` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='user_formation';

--
-- Indexes for dumped tables
--

--
-- Indexes for table `competence`
--
ALTER TABLE `competence`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `experience`
--
ALTER TABLE `experience`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `formation`
--
ALTER TABLE `formation`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `language`
--
ALTER TABLE `language`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `odf`
--
ALTER TABLE `odf`
 ADD PRIMARY KEY (`word`);

--
-- Indexes for table `otf`
--
ALTER TABLE `otf`
 ADD PRIMARY KEY (`word`,`offer`);

--
-- Indexes for table `session`
--
ALTER TABLE `session`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `competence`
--
ALTER TABLE `competence`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `experience`
--
ALTER TABLE `experience`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `formation`
--
ALTER TABLE `formation`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `language`
--
ALTER TABLE `language`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `session`
--
ALTER TABLE `session`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=63;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=17;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
