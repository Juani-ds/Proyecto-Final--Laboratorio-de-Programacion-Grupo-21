-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3310
-- Tiempo de generación: 23-10-2025 a las 01:55:40
-- Versión del servidor: 10.1.38-MariaDB
-- Versión de PHP: 5.6.40

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gp21_cinemacentro`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comprador`
--

CREATE TABLE `comprador` (
  `dni` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `fechaNac` date DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `medioPago` varchar(50) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_lugar`
--

CREATE TABLE `detalle_lugar` (
  `codDetalle` int(11) NOT NULL,
  `codLugar` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_ticket`
--

CREATE TABLE `detalle_ticket` (
  `codDetalle` int(11) NOT NULL,
  `idTicket` int(11) NOT NULL,
  `idProyeccion` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `lugar`
--

CREATE TABLE `lugar` (
  `codLugar` int(11) NOT NULL,
  `idProyeccion` int(11) NOT NULL,
  `fila` char(1) NOT NULL,
  `numero` int(11) NOT NULL,
  `estado` varchar(20) DEFAULT 'Disponible'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pelicula`
--

CREATE TABLE `pelicula` (
  `idPelicula` int(11) NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `director` varchar(100) DEFAULT NULL,
  `actores` text,
  `origen` varchar(50) DEFAULT NULL,
  `genero` varchar(50) DEFAULT NULL,
  `estreno` date DEFAULT NULL,
  `enCartelera` tinyint(1) DEFAULT '1',
  `activo` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proyeccion`
--

CREATE TABLE `proyeccion` (
  `idProyeccion` int(11) NOT NULL,
  `idPelicula` int(11) NOT NULL,
  `nroSala` int(11) NOT NULL,
  `idioma` varchar(50) DEFAULT NULL,
  `es3D` tinyint(1) DEFAULT '0',
  `subtitulada` tinyint(1) DEFAULT '0',
  `horaInicio` datetime NOT NULL,
  `horaFin` datetime NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `precioLugar` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sala`
--

CREATE TABLE `sala` (
  `nroSala` int(11) NOT NULL,
  `apta3D` tinyint(1) DEFAULT '0',
  `capacidad` int(11) DEFAULT NULL,
  `estado` varchar(20) DEFAULT 'Disponible',
  `activo` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ticket_compra`
--

CREATE TABLE `ticket_compra` (
  `idTicket` int(11) NOT NULL,
  `dniComprador` varchar(20) NOT NULL,
  `fechaCompra` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fechaFuncion` datetime NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  `tipoCompra` varchar(20) NOT NULL,
  `medioPago` varchar(20) DEFAULT NULL,
  `codigoVenta` varchar(50) DEFAULT NULL,
  `estadoTicket` varchar(20) DEFAULT 'Pendiente'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `comprador`
--
ALTER TABLE `comprador`
  ADD PRIMARY KEY (`dni`);

--
-- Indices de la tabla `detalle_lugar`
--
ALTER TABLE `detalle_lugar`
  ADD PRIMARY KEY (`codDetalle`,`codLugar`),
  ADD KEY `codLugar` (`codLugar`);

--
-- Indices de la tabla `detalle_ticket`
--
ALTER TABLE `detalle_ticket`
  ADD PRIMARY KEY (`codDetalle`),
  ADD KEY `idTicket` (`idTicket`),
  ADD KEY `idProyeccion` (`idProyeccion`);

--
-- Indices de la tabla `lugar`
--
ALTER TABLE `lugar`
  ADD PRIMARY KEY (`codLugar`),
  ADD UNIQUE KEY `unique_asiento` (`idProyeccion`,`fila`,`numero`);

--
-- Indices de la tabla `pelicula`
--
ALTER TABLE `pelicula`
  ADD PRIMARY KEY (`idPelicula`);

--
-- Indices de la tabla `proyeccion`
--
ALTER TABLE `proyeccion`
  ADD PRIMARY KEY (`idProyeccion`),
  ADD KEY `idPelicula` (`idPelicula`),
  ADD KEY `nroSala` (`nroSala`);

--
-- Indices de la tabla `sala`
--
ALTER TABLE `sala`
  ADD PRIMARY KEY (`nroSala`);

--
-- Indices de la tabla `ticket_compra`
--
ALTER TABLE `ticket_compra`
  ADD PRIMARY KEY (`idTicket`),
  ADD UNIQUE KEY `codigoVenta` (`codigoVenta`),
  ADD KEY `dniComprador` (`dniComprador`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `detalle_ticket`
--
ALTER TABLE `detalle_ticket`
  MODIFY `codDetalle` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `lugar`
--
ALTER TABLE `lugar`
  MODIFY `codLugar` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pelicula`
--
ALTER TABLE `pelicula`
  MODIFY `idPelicula` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `proyeccion`
--
ALTER TABLE `proyeccion`
  MODIFY `idProyeccion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `sala`
--
ALTER TABLE `sala`
  MODIFY `nroSala` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ticket_compra`
--
ALTER TABLE `ticket_compra`
  MODIFY `idTicket` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `detalle_lugar`
--
ALTER TABLE `detalle_lugar`
  ADD CONSTRAINT `detalle_lugar_ibfk_1` FOREIGN KEY (`codDetalle`) REFERENCES `detalle_ticket` (`codDetalle`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_lugar_ibfk_2` FOREIGN KEY (`codLugar`) REFERENCES `lugar` (`codLugar`) ON DELETE CASCADE;

--
-- Filtros para la tabla `detalle_ticket`
--
ALTER TABLE `detalle_ticket`
  ADD CONSTRAINT `detalle_ticket_ibfk_1` FOREIGN KEY (`idTicket`) REFERENCES `ticket_compra` (`idTicket`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_ticket_ibfk_2` FOREIGN KEY (`idProyeccion`) REFERENCES `proyeccion` (`idProyeccion`);

--
-- Filtros para la tabla `lugar`
--
ALTER TABLE `lugar`
  ADD CONSTRAINT `lugar_ibfk_1` FOREIGN KEY (`idProyeccion`) REFERENCES `proyeccion` (`idProyeccion`);

--
-- Filtros para la tabla `proyeccion`
--
ALTER TABLE `proyeccion`
  ADD CONSTRAINT `proyeccion_ibfk_1` FOREIGN KEY (`idPelicula`) REFERENCES `pelicula` (`idPelicula`),
  ADD CONSTRAINT `proyeccion_ibfk_2` FOREIGN KEY (`nroSala`) REFERENCES `sala` (`nroSala`);

--
-- Filtros para la tabla `ticket_compra`
--
ALTER TABLE `ticket_compra`
  ADD CONSTRAINT `ticket_compra_ibfk_1` FOREIGN KEY (`dniComprador`) REFERENCES `comprador` (`dni`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
