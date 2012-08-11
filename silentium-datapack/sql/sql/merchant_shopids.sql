DROP TABLE IF EXISTS merchant_shopids;

CREATE TABLE merchant_shopids (
  shop_id decimal(9,0) NOT NULL default '0',
  npc_id varchar(9) default NULL,
  PRIMARY KEY  (shop_id)
);

-- General Shops
INSERT INTO `merchant_shopids` VALUES
(1,'30001'),
(2,'30001'),
(3,'30087'),
(4,'30087'),
(5,'30088'),
(6,'30088'),
(7,'30090'),
(8,'30091'),
(9,'30093'),
(10,'30093'),
(11,'30002'),
(12,'30002'),
(13,'30003'),
(14,'30004'),
(15,'30060'),
(16,'30060'),
(17,'30061'),
(18,'30061'),
(19,'30062'),
(20,'30063'),
(21,'30165'),
(22,'30078'),
(23,'30081'),
(24,'30082'),
(25,'30084'),
(26,'30084'),
(27,'30085'),
(28,'30085'),
(29,'30094'),
(30,'30094'),
(31,'30135'),
(32,'30135'),
(33,'30136'),
(34,'30136'),
(35,'30137'),
(36,'30138'),
(37,'30147'),
(38,'30147'),
(39,'30148'),
(40,'30148'),
(41,'30149'),
(42,'30150'),
(43,'30163'),
(44,'30164'),
(45,'30165'),
(46,'30166'),
(47,'30178'),
(48,'30178'),
(49,'30179'),
(50,'30179'),
(51,'30180'),
(52,'30181'),
(53,'30207'),
(54,'30207'),
(55,'30208'),
(56,'30208'),
(57,'30209'),
(58,'30230'),
(59,'30230'),
(60,'30231'),
(61,'30253'),
(62,'30253'),
(63,'30254'),
(64,'30294'),
(65,'30301'),
(66,'30313'),
(67,'30314'),
(68,'30315'),
(69,'30321'),
(70,'30321'),
(71,'30420'),
(72,'30436'),
(73,'30834'),
(74,'30437'),
(75,'30516'),
(76,'30516'),
(77,'30517'),
(78,'30517'),
(79,'30518'),
(80,'30519'),
(81,'30558'),
(82,'30558'),
(83,'30559'),
(84,'30559'),
(85,'30560'),
(86,'30561'),
(87,'30684'),
(88,'30684'),
(89,'30731'),
(90,'30827'),
(91,'30828'),
(92,'30829'),
(93,'30830'),
(94,'30831'),
(95,'30834'),
(96,'30837'),
(97,'30837'),
(98,'30838'),
(99,'30838'),
(100,'30839'),
(101,'30840'),
(102,'30841'),
(103,'30842'),
(104,'30869'),
(105,'31256'),
(106,'31256'),
(107,'31257'),
(108,'31257'),
(109,'31258'),
(110,'31258'),
(111,'31259'),
(112,'31259'),
(113,'31260'),
(114,'31261'),
(115,'31262'),
(116,'31263'),
(117,'31263'),
(118,'31265'),
(119,'31273'),
(120,'31274'),
(121,'31284'),
(122,'31291'),
(123,'31300'),
(124,'31300'),
(125,'31301'),
(126,'31301'),
(127,'31302'),
(128,'31302'),
(129,'31303'),
(130,'31303'),
(131,'31304'),
(132,'31305'),
(133,'31306'),
(134,'31307'),
(135,'31307'),
(136,'31309'),
(137,'31318'),
(138,'31319'),
(139,'31338'),
(140,'31339'),
(141,'31366'),
(145,'31366'),
(146,'31445'),
(147,'31386'),
(148,'31438'),
(149,'31413'),
(150,'31419'),
(151,'31666'),
(152,'31431'),
(153,'31954'),
(154,'31441'),
(155,'31442'),
(156,'31444'),
(157,'31669'),
(158,'31963'),
(159,'31414'),
(160,'31418'),
(161,'31415'),
(162,'31423'),
(163,'31433'),
(164,'31440'),
(165,'31432'),
(166,'31425'),
(167,'31439'),
(168,'31954'),
(169,'31962'),
(170,'31416'),
(171,'31417'),
(172,'31435'),
(173,'31437'),
(174,'31422'),
(175,'31668'), 
(176,'31434'), 
(177,'31426'), 
(178,'31428'),
(179,'31945'),
(180,'31945'),
(181,'31946'),
(182,'31946'),
(183,'31947'),
(184,'31947'),
(185,'31948'),
(186,'31948'),
(187,'31949'),
(188,'31950'),
(189,'31951'),
(190,'31952'),
(191,'31952'),
(192,'31962'),
(193,'31963'),
(194,'31973'),
(195,'31980'),
(196,'31670'),
(197,'31420'),
(198,'31427'),
(199,'31436'),
(200,'31443'),
(201,'31429'),
(202,'31421'),
(203,'31430'),
(204,'31667'),
(205,'30314'),
(351,'30047'),
(352,'30387'),
(353,'30879'),
(354,'31351'),
(355,'30003'),
(359,'31414'),
(360,'30149'),
(361,'31415'),
(362,'30138'),
(363,'30560'),
(364,'31424'),
(365,'32106'),
(366,'30062'),
(367,'30063'),
(368,'30081'),
(369,'30082'),
(370,'30180'),
(371,'30181'),
(372,'30254'),
(373,'30294'),
(374,'30301'),
(375,'30841'),
(376,'30842'),
(377,'30892'),
(378,'30893'),
(379,'30166'),
(380,'30231'),
(381,'32105'),
(382,'31380'),
(383,'31373'), 
(5600,'30892'),
(5601,'30893'),
(5710,'31067'),
(5800,'30890'),
(5801,'30890'),
(5802,'30891'),
(5803,'30891'),
(5804,'31044'),
(5805,'31045'),
(350071,'35007');

-- Mercenary Managers
INSERT INTO `merchant_shopids` VALUES
(351021,'35102'),
(351441,'35144'),
(351861,'35186'),
(352281,'35228'),
(352761,'35276'),
(353181,'35318'),
(353651,'35365');

-- Fishermens
INSERT INTO `merchant_shopids` VALUES
(142,'31578'),
(143,'31579'),
(144,'31696'),
(400,'31562'), 
(401,'31563'), 
(402,'31564'), 
(403,'31565'), 
(404,'31566'), 
(405,'31567'), 
(406,'31568'), 
(407,'31569'), 
(408,'31570'), 
(409,'31571'), 
(410,'31572'), 
(411,'31573'), 
(412,'31574'), 
(413,'31575'), 
(414,'31576'), 
(415,'31577'), 
(416,'31578'), 
(417,'31579'), 
(418,'31616'), 
(419,'31696'), 
(420,'31697'),
(421,'31989'),
(423,'32105');

-- GM Shops
INSERT INTO `merchant_shopids` VALUES
(1001,'gm'),
(1002,'gm'),
(1003,'gm'),
(1004,'gm'),
(1005,'gm'),
(1006,'gm'),
(1007,'gm'),
(1008,'gm'),
(1009,'gm'),
(1010,'gm'),
(1011,'gm'),
(1012,'gm'),
(1013,'gm'),
(1014,'gm'),
(1015,'gm'),
(1020,'gm'),
(2011,'gm'),
(2012,'gm'),
(2013,'gm'),
(2014,'gm'),
(2015,'gm'),
(3001,'gm'),
(3002,'gm'),
(3003,'gm'),
(9001,'gm'),
(9002,'gm'),
(9003,'gm'),
(9004,'gm'),
(9005,'gm'),
(9006,'gm'),
(9007,'gm'),
(9008,'gm'),
(9009,'gm'),
(9010,'gm'),
(9011,'gm'),
(9012,'gm'),
(9013,'gm'),
(9014,'gm'),
(9015,'gm'),
(9016,'gm'),
(9017,'gm'),
(9018,'gm'),
(9019,'gm'),
(9020,'gm'),
(9021,'gm'),
(9022,'gm'),
(9023,'gm'),
(9024,'gm'),
(9025,'gm'),
(9026,'gm'),
(9027,'gm'),
(9028,'gm'),
(9029,'gm'),
(9030,'gm'),
(9031,'gm'),
(9032,'gm'),
(9033,'gm'),
(9034,'gm'),
(9035,'gm'),
(9036,'gm'),
(9037,'gm'),
(9038,'gm'),
(9039,'gm'),
(9040,'gm'),
(9041,'gm'),
(9042,'gm'),
(9043,'gm'),
(9044,'gm'),
(9045,'gm'),
(9046,'gm'),
(9047,'gm'),
(9048,'gm'),
(9049,'gm'),
(9050,'gm'),
(9051,'gm'),
(9052,'gm'),
(9053,'gm'),
(9054,'gm'),
(9055,'gm'),
(9056,'gm'),
(9057,'gm'),
(9058,'gm'),
(9059,'gm'),
(9060,'gm'),
(9061,'gm'),
(9062,'gm'),
(9063,'gm'),
(9064,'gm'),
(9065,'gm'),
(9066,'gm'),
(9067,'gm'),
(9068,'gm'),
(9069,'gm'),
(9070,'gm'),
(9071,'gm'),
(9072,'gm'),
(9073,'gm'),
(9074,'gm'),
(9075,'gm'),
(9076,'gm'),
(9077,'gm'),
(9078,'gm'),
(9079,'gm'),
(9080,'gm'),
(9081,'gm'),
(9082,'gm'),
(9083,'gm'),
(9084,'gm'),
(9085,'gm'),
(9086,'gm'),
(9087,'gm'),
(9088,'gm'),
(9089,'gm'),
(9090,'gm'),
(9091,'gm'),
(9092,'gm'),
(9093,'gm'),
(9094,'gm'),
(9095,'gm'),
(9096,'gm'),
(9097,'gm'),
(9098,'gm'),
(9099,'gm'),
(9100,'gm'),
(9101,'gm'),
(9102,'gm'),
(9103,'gm'),
(9104,'gm'),
(9105,'gm'),
(9106,'gm'),
(9107,'gm'),
(9108,'gm'),
(9109,'gm'),
(9110,'gm'),
(9111,'gm'),
(9112,'gm'),
(9113,'gm'),
(9114,'gm'),
(9115,'gm'),
(9116,'gm'),
(9117,'gm'),
(9118,'gm'),
(9119,'gm'),
(9120,'gm'),
(9121,'gm'),
(9122,'gm'),
(9123,'gm'),
(9124,'gm'),
(9125,'gm'),
(9126,'gm'),
(9127,'gm'),
(9128,'gm'),
(9148,'gm'),
(9149,'gm'),
(9150,'gm'),
(30040,'gm'),
(30041,'gm'),
(30042,'gm'),
(30043,'gm'),
(30044,'gm'),
(30045,'gm'),
(30046,'gm'),
(30047,'gm'),
(30048,'gm'),
(30049,'gm'),
(30050,'gm'),
(30051,'gm'),
(30052,'gm'),
(30053,'gm'),
(30054,'gm'),
(30055,'gm'),
(30056,'gm'),
(30057,'gm'),
(30058,'gm'),
(30059,'gm'),
(71021,'gm'),
(71022,'gm'),
(71023,'gm'),
(71024,'gm'),
(71025,'gm'),
(71026,'gm'),
(71027,'gm'),
(71028,'gm'),
(71029,'gm'),
(71030,'gm'),
(300523,'gm'),
(300524,'gm'),
(300525,'gm'),
(300526,'gm'),
(300527,'gm'),
(300528,'gm'),
(300529,'gm'),
(300530,'gm'),
(300531,'gm'),
(300532,'gm'),
(300533,'gm'),
(300534,'gm'),
(300535,'gm'),
(300536,'gm'),
(300537,'gm'),
(300538,'gm'),
(300539,'gm'),
(300540,'gm'),
(300541,'gm'),
(300543,'gm'),
(300522,'gm'),
(300511,'gm'),
(300510,'gm'),
(300410,'gm'),
(300542,'gm'),
(71031, 'gm'),
(71032, 'gm');

-- Castle
INSERT INTO `merchant_shopids` VALUES
(335103, '35103'),
(335145, '35145'),
(335187, '35187'),
(335229, '35229'),
(335230, '35230'),
(335231, '35231'),
(335277, '35277'),
(335319, '35319'),
(335366, '35366'),
(335512, '35512'),
(335558, '35558'),
(335644, '35644'),
(335645, '35645');

-- Castles Item creation
INSERT INTO `merchant_shopids` VALUES
(351001,'35100'),
(351002,'35100'),
(351421,'35142'),
(351422,'35142'),
(351841,'35184'),
(351842,'35184'),
(352261,'35226'),
(352262,'35226'),
(352741,'35274'),
(352742,'35274'),
(353161,'35316'),
(353162,'35316'),
(353631,'35363'),
(353632,'35363'),
(355091,'35509'),
(355092,'35509'),
(355551,'35555'),
(355552,'35555');

-- Clan Halls Item creation
INSERT INTO `merchant_shopids` VALUES
(135445, '35445'),
(235445, '35445'),
(335445, '35445'),
(135453, '35453'),
(235453, '35453'),
(335453, '35453'),
(135455, '35455'),
(235455, '35455'),
(335455, '35455'),
(135451, '35451'),
(235451, '35451'),
(335451, '35451'),
(135457, '35457'),
(235457, '35457'),
(335457, '35457'),
(135459, '35459'),
(235459, '35459'),
(335459, '35459'),
(135383, '35383'),
(235383, '35383'),
(335383, '35383'),
(135398, '35398'),
(235398, '35398'),
(335398, '35398'),
(135400, '35400'),
(235400, '35400'),
(335400, '35400'),
(135392, '35392'),
(235392, '35392'),
(335392, '35392'),
(135394, '35394'),
(235394, '35394'),
(335394, '35394'),
(135396, '35396'),
(235396, '35396'),
(335396, '35396'),
(135384, '35384'),
(235384, '35384'),
(335384, '35384'),
(135390, '35390'),
(235390, '35390'),
(335390, '35390'),
(135386, '35386'),
(235386, '35386'),
(335386, '35386'),
(135388, '35388'),
(235388, '35388'),
(335388, '35388'),
(135407, '35407'),
(235407, '35407'),
(335407, '35407'),
(135403, '35403'),
(235403, '35403'),
(335403, '35403'),
(135405, '35405'),
(235405, '35405'),
(335405, '35405'),
(135421, '35421'),
(235421, '35421'),
(335421, '35421'),
(135439, '35439'),
(235439, '35439'),
(335439, '35439'),
(135441, '35441'),
(235441, '35441'),
(335441, '35441'),
(135443, '35443'),
(235443, '35443'),
(335443, '35443'),
(135447, '35447'),
(235447, '35447'),
(335447, '35447'),
(135449, '35449'),
(235449, '35449'),
(335449, '35449'),
(135467, '35467'),
(235467, '35467'),
(335467, '35467'),
(135465, '35465'),
(235465, '35465'),
(335465, '35465'),
(135463, '35463'),
(235463, '35463'),
(335463, '35463'),
(135461, '35461'),
(235461, '35461'),
(335461, '35461'),
(335566, '35566'),
(235566, '35566'),
(135566, '35566'),
(335568, '35568'),
(235568, '35568'),
(135568, '35568'),
(335570, '35570'),
(235570, '35570'),
(135570, '35570'),
(335572, '35572'),
(235572, '35572'),
(135572, '35572'),
(335574, '35574'),
(235574, '35574'),
(135574, '35574'),
(335576, '35576'),
(235576, '35576'),
(135576, '35576'),
(335578, '35578'),
(235578, '35578'),
(135578, '35578'),
(235580, '35580'),
(135580, '35580'),
(335580, '35580'),
(335582, '35582'),
(235582, '35582'),
(135582, '35582'),
(135584, '35584'),
(235584, '35584'),
(335584, '35584'),
(335586, '35586'),
(135586, '35586'),
(235586, '35586'),
(355111, '35511'),
(355571, '35557');