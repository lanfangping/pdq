<?xml version="1.0" encoding="UTF-8"?>
<schema name="2" description="">
<relations>
<relation name="YahooPlaces" source="yahoo" size="10000000">
	<attribute name="woeid"    type="java.lang.Integer"     input-method="woeid.1"    path="woeid"/>
		<attribute name="name"     type="java.lang.String"      input-method="keywords.1" path="name"/>
		<attribute name="type"     type="java.lang.Integer"     input-method="type.1"     path="placeTypeName attrs/code"/>

		<attribute name="placeTypeName" type="java.lang.String" path="placeTypeName"/>
		<attribute name="country"       type="java.lang.String" path="country"/>
		<attribute name="admin1"        type="java.lang.String" path="admin1"/>
		<attribute name="admin2"        type="java.lang.String" path="admin2"/>
		<attribute name="admin3"        type="java.lang.String" path="admin3"/>
		<attribute name="locality1"     type="java.lang.String" path="locality1"/>
		<attribute name="locality2"     type="java.lang.String" path="locality2"/>
		<attribute name="postal"        type="java.lang.String" path="postal"/>
		<attribute name="centroid_lat"  type="java.lang.Double" path="centroid/latitude"/>
		<attribute name="centroid_lng"  type="java.lang.Double" path="centroid/longitude"/>
		<attribute name="bboxNorth"     type="java.lang.Double" path="northEast/latitude"/>
		<attribute name="bboxSouth"     type="java.lang.Double" path="southWest/latitude"/>
		<attribute name="bboxEast"      type="java.lang.Double" path="northEast/longitude"/>
		<attribute name="bboxWest"      type="java.lang.Double" path="southWest/longitude"/>
		<attribute name="timezone"      type="java.lang.String" path="timezone"/>
<access-method name="yh_geo_name" type="LIMITED" inputs="1" cost="100.0"/>
<access-method name="yh_geo_woeid" type="LIMITED" inputs="0" cost="1.0"/>
<access-method name="yh_geo_type" type="LIMITED" inputs="1,2" cost="50.0"/>
</relation>
<relation name="YahooPlaceType" source="yahoo" size="20">
		<attribute name="placeTypeName2" type="java.lang.String"  path="placeTypeName"/>
		<attribute name="code"          type="java.lang.Integer" path="placeTypeName attrs/code" input-method="type.1"/>
		<attribute name="uri"           type="java.lang.String"  path="uri"/>
<access-method name="yh_geo_types" type="FREE" cost="50.0"/>
<access-method name="yh_geo_types_name" type="LIMITED" inputs="1" cost="5.0"/>
</relation>
<relation name="YahooPlaceCommonAncestor" source="yahoo" size="10000000">
		<attribute    name="woeid1"         type="java.lang.Integer" input-method="relation.1" />
		<static-input name="commonAncestor" type="java.lang.String"  input-method="relation.2" value="common"/>
		<attribute    name="woeid2"         type="java.lang.Integer" input-method="inline.1" />

		<attribute name="woeid"         type="java.lang.Integer" path="woeid"/>
		<attribute name="placeTypeName3" type="java.lang.String"  path="placeTypeName"/>
		<attribute name="name"          type="java.lang.String"  path="name"/>
		<attribute name="uri"           type="java.lang.String"  path="uri"/>
<access-method name="yh_com_anc" type="LIMITED" inputs="0,1" cost="25.0"/>
</relation>
<relation name="YahooPlaceRelationship" source="yahoo" size="10000000">
		<attribute name="relation"      type="java.lang.String"  input-method="relation.2"/>
		<attribute name="of"            type="java.lang.Integer" input-method="relation.1" />
		<attribute name="woeid"         type="java.lang.Integer" path="woeid"/>
		<attribute name="placeTypeName4" type="java.lang.String"  path="placeTypeName"/>
		<attribute name="name4"          type="java.lang.String"  path="name"/>
		<attribute name="uri4"           type="java.lang.String"  path="uri"/>
<access-method name="yh_geo_rel" type="LIMITED" inputs="0,1" cost="50.0"/>
</relation>
<relation name="YahooPlaceCode" source="yahoo" size="10000000">
		<attribute name="namespace" type="java.lang.String"  input-method="relation.1"/>
		<attribute name="code4"      type="java.lang.String"  input-method="relation.2"/>
		<attribute name="woeid"     type="java.lang.Integer" path="woeid"/>
		<access-method name="yh_geo_code" type="LIMITED" inputs="0,1" cost="1.0"/>
</relation>
<relation name="YahooContinents" source="yahoo" size="7">
		<attribute name="woeid"         type="java.lang.Integer" path="woeid"/>
		<attribute name="placeType"     type="java.lang.Integer" path="placeTypeName attrs/code"/>
		<attribute name="placeTypeName5" type="java.lang.String"  path="placeTypeName"/>
		<attribute name="name5"          type="java.lang.String"  path="name"/>
<access-method name="yh_geo_continent" type="FREE" cost="1.0"/>
</relation>
<relation name="YahooCountries" source="yahoo" size="250">
		<attribute name="woeid"         type="java.lang.Integer" path="woeid"/>
		<attribute name="placeType"     type="java.lang.Integer" path="placeTypeName attrs/code"/>
		<attribute name="placeTypeName6" type="java.lang.String"  path="placeTypeName"/>
		<attribute name="name6
		"          type="java.lang.String"  path="name"/>
		<access-method name="yh_geo_country" type="FREE" cost="1.0"/>
</relation>
<relation name="YahooSeas" source="yahoo" size="51">
		<attribute name="woeid"         type="java.lang.Integer" path="woeid"/>
		<attribute name="placeType"     type="java.lang.Integer" path="placeTypeName attrs/code"/>
		<attribute name="placeTypeName7" type="java.lang.String"  path="placeTypeName"/>
		<attribute name="name7"          type="java.lang.String"  path="name"/>
<access-method name="yh_geo_sea" type="FREE" cost="1.0"/>
</relation>
<relation name="YahooOceans" source="yahoo" size="5">
		<attribute name="woeid"         type="java.lang.Integer" path="woeid"/>
		<attribute name="placeType"     type="java.lang.Integer" path="placeTypeName attrs/code"/>
		<attribute name="placeTypeName8" type="java.lang.String"  path="placeTypeName"/>
		<attribute name="name8"          type="java.lang.String"  path="name"/>
<access-method name="yh_geo_ocean" type="FREE" cost="1.0"/>
</relation>
<relation name="YahooWeather" source="yahoo" size="100000000">
		<attribute name="woeid"          type="java.lang.Integer" input-method="q.2" />

		<attribute name="city"           type="java.lang.String"  path="location/city"/>
		<attribute name="country2"        type="java.lang.String"  path="location/country"/>
		<attribute name="region"         type="java.lang.String"  path="location/region"/>

		<attribute name="distance_unit"  type="java.lang.String"  path="units/distance"/>
		<attribute name="pressure_unit"  type="java.lang.String"  path="units/pressure"/>
		<attribute name="speed_unit"     type="java.lang.String"  path="units/speed"/>
		<attribute name="temp_unit"      type="java.lang.String"  path="units/temperature"/>

		<attribute name="wind_chill"     type="java.lang.Integer" path="wind/chill"/>
		<attribute name="wind_direction" type="java.lang.Integer" path="wind/direction"/>
		<attribute name="wind_speed"     type="java.lang.String"  path="wind/speed"/>

		<attribute name="humidity"       type="java.lang.Double"  path="atmosphere/humidity"/>
		<attribute name="pressure"       type="java.lang.Double"  path="atmosphere/pressure"/>
		<attribute name="rising"         type="java.lang.Integer" path="atmosphere/rising"/>
		<attribute name="visibility"     type="java.lang.Double"  path="atmosphere/visibility"/>

		<attribute name="sunrise"        type="java.lang.String"  path="astronomy/sunrise"/>
		<attribute name="sunset"         type="java.lang.String"  path="astronomy/sunset"/>

		<attribute name="date"           type="java.lang.String"  path="item/condition/date"/>
		<attribute name="temperature"    type="java.lang.Double"  path="item/condition/temp"/>
		<attribute name="condition"      type="java.lang.String"  path="item/condition/text"/>
		<attribute name="code2"           type="java.lang.Integer" path="item/condition/code"/>
<access-method name="yh_wtr_woeid" type="LIMITED" inputs="0" cost="10.0"/>
</relation>
</relations>
<dependencies>
<dependency>
<body>
<atom name="YahooPlaces">
<variable name="woeid"/>
<variable name="name"/>
<variable name="type"/>
<variable name="placeTypeName"/>
<variable name="country"/>
<variable name="admin1"/>
<variable name="admin2"/>
<variable name="admin3"/>
<variable name="locality1"/>
<variable name="locality2"/>
<variable name="postal"/>
<variable name="latitude"/>
<variable name="longitude"/>
<variable name="bboxNorth"/>
<variable name="bboxSouth"/>
<variable name="bboxEast"/>
<variable name="bboxWest"/>
<variable name="timezone"/>
</atom>
</body>
<head>
<atom name="YahooPlaceType">
<variable name="placeTypeName"/>
<variable name="type"/>
<variable name="uri"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaceRelationship">
<constant value="descendants"/>
<variable name="x"/>
<variable name="y"/>
<variable name="x1"/>
<variable name="x2"/>
<variable name="x3"/>
</atom>
<atom name="YahooPlaceRelationship">
<constant value="children"/>
<variable name="y"/>
<variable name="z"/>
<variable name="y1"/>
<variable name="y2"/>
<variable name="y3"/>
</atom>
</body>
<head>
<atom name="YahooPlaceRelationship">
<constant value="descendants"/>
<variable name="x"/>
<variable name="z"/>
<variable name="z1"/>
<variable name="z2"/>
<variable name="z3"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaceRelationship">
<constant value="children"/>
<variable name="x"/>
<variable name="y"/>
<variable name="x1"/>
<variable name="x2"/>
<variable name="x3"/>
</atom>
<atom name="YahooPlaceRelationship">
<constant value="children"/>
<variable name="y"/>
<variable name="z"/>
<variable name="y1"/>
<variable name="y2"/>
<variable name="y3"/>
</atom>
</body>
<head>
<atom name="YahooPlaceRelationship">
<constant value="descendants"/>
<variable name="x"/>
<variable name="z"/>
<variable name="z1"/>
<variable name="z2"/>
<variable name="z3"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaceRelationship">
<constant value="ancestors"/>
<variable name="x"/>
<variable name="y"/>
<variable name="x1"/>
<variable name="x2"/>
<variable name="x3"/>
</atom>
</body>
<head>
<atom name="YahooPlaceRelationship">
<constant value="descendants"/>
<variable name="y"/>
<variable name="x"/>
<variable name="y1"/>
<variable name="y2"/>
<variable name="y3"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaceRelationship">
<constant value="ancestors"/>
<variable name="x"/>
<variable name="y"/>
<variable name="x1"/>
<variable name="x2"/>
<variable name="x3"/>
</atom>
<atom name="YahooPlaceRelationship">
<constant value="ancestors"/>
<variable name="z"/>
<variable name="y"/>
<variable name="y1"/>
<variable name="y2"/>
<variable name="y3"/>
</atom>
</body>
<head>
<atom name="YahooPlaceCommonAncestor">
<variable name="x"/>
<variable name="z"/>
<variable name="y"/>
<variable name="w2"/>
<variable name="w3"/>
<variable name="w4"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaceCode">
<variable name="namespace"/>
<variable name="code"/>
<variable name="woeid"/>
</atom>
</body>
<head>
<atom name="YahooPlaces">
<variable name="woeid"/>
<variable name="name"/>
<variable name="type"/>
<variable name="placeTypeName"/>
<variable name="country"/>
<variable name="admin1"/>
<variable name="admin2"/>
<variable name="admin3"/>
<variable name="locality1"/>
<variable name="locality2"/>
<variable name="postal"/>
<variable name="latitude"/>
<variable name="longitude"/>
<variable name="bboxNorth"/>
<variable name="bboxSouth"/>
<variable name="bboxEast"/>
<variable name="bboxWest"/>
<variable name="timezone"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaces">
<variable name="woeid"/>
<variable name="name"/>
<variable name="type"/>
<constant value="Continent"/>
<variable name="country"/>
<variable name="admin1"/>
<variable name="admin2"/>
<variable name="admin3"/>
<variable name="locality1"/>
<variable name="locality2"/>
<variable name="postal"/>
<variable name="latitude"/>
<variable name="longitude"/>
<variable name="bboxNorth"/>
<variable name="bboxSouth"/>
<variable name="bboxEast"/>
<variable name="bboxWest"/>
<variable name="timezone"/>
</atom>
</body>
<head>
<atom name="YahooContinents">
<variable name="woeid"/>
<variable name="type"/>
<variable name="placeTypeName"/>
<variable name="name"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaces">
<variable name="woeid"/>
<variable name="name"/>
<variable name="type"/>
<constant value="Country"/>
<variable name="country"/>
<variable name="admin1"/>
<variable name="admin2"/>
<variable name="admin3"/>
<variable name="locality1"/>
<variable name="locality2"/>
<variable name="postal"/>
<variable name="latitude"/>
<variable name="longitude"/>
<variable name="bboxNorth"/>
<variable name="bboxSouth"/>
<variable name="bboxEast"/>
<variable name="bboxWest"/>
<variable name="timezone"/>
</atom>
</body>
<head>
<atom name="YahooCountries">
<variable name="woeid"/>
<variable name="type"/>
<variable name="placeTypeName"/>
<variable name="name"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaces">
<variable name="woeid"/>
<variable name="name"/>
<variable name="type"/>
<constant value="Sea"/>
<variable name="country"/>
<variable name="admin1"/>
<variable name="admin2"/>
<variable name="admin3"/>
<variable name="locality1"/>
<variable name="locality2"/>
<variable name="postal"/>
<variable name="latitude"/>
<variable name="longitude"/>
<variable name="bboxNorth"/>
<variable name="bboxSouth"/>
<variable name="bboxEast"/>
<variable name="bboxWest"/>
<variable name="timezone"/>
</atom>
</body>
<head>
<atom name="YahooSeas">
<variable name="woeid"/>
<variable name="type"/>
<variable name="placeTypeName"/>
<variable name="name"/>
</atom>
</head>
</dependency>
<dependency>
<body>
<atom name="YahooPlaces">
<variable name="woeid"/>
<variable name="name"/>
<variable name="type"/>
<constant value="Ocean"/>
<variable name="country"/>
<variable name="admin1"/>
<variable name="admin2"/>
<variable name="admin3"/>
<variable name="locality1"/>
<variable name="locality2"/>
<variable name="postal"/>
<variable name="latitude"/>
<variable name="longitude"/>
<variable name="bboxNorth"/>
<variable name="bboxSouth"/>
<variable name="bboxEast"/>
<variable name="bboxWest"/>
<variable name="timezone"/>
</atom>
</body>
<head>
<atom name="YahooOceans">
<variable name="woeid"/>
<variable name="type"/>
<variable name="placeTypeName"/>
<variable name="name"/>
</atom>
</head>
</dependency>
</dependencies>
</schema>
