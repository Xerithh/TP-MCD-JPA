-- Données de base pour le projet Pharmacie
-- Dispensaire (Etablissements de santé qui passent commande de médicaments)
-- Le fichier est chargé au démarrage de l''application

-- Insertion des catégories de médicaments
INSERT INTO CATEGORIE (CODE, LIBELLE, DESCRIPTION) VALUES
(DEFAULT, 'Antalgiques et Antipyrétiques', 'Médicaments contre la douleur et la fièvre'), -- code : 1
(DEFAULT, 'Anti-inflammatoires', 'Médicaments réduisant l''inflammation'), -- code : 2
(DEFAULT, 'Antibiotiques', 'Médicaments pour traiter les infections bactériennes'),
(DEFAULT, 'Antihypertenseurs', 'Médicaments pour traiter l''hypertension artérielle'),
(DEFAULT, 'Antidiabétiques', 'Médicaments pour traiter le diabète'),
(DEFAULT, 'Antihistaminiques', 'Médicaments pour traiter les allergies'),
(DEFAULT, 'Vitamines et Compléments', 'Suppléments nutritionnels'),
(DEFAULT, 'Médicaments Cardiovasculaires', 'Médicaments pour le cœur et la circulation'),
(DEFAULT, 'Médicaments Gastro-intestinaux', 'Médicaments pour les troubles digestifs'),
(DEFAULT, 'Médicaments Respiratoires', 'Médicaments pour les troubles respiratoires');


-- Catégorie 1: Antalgiques et Antipyrétiques
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Morphine 10mg', 1, 'Boîte de 14 comprimés', 25.80, 80, 0, 15, false, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400'),
('Doliprane Effervescent 1g', 1, 'Boîte de 8 comprimés', 3.50, 280, 0, 30, false, 'https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=400'),
('Efferalgan Vitamine C', 1, 'Boîte de 16 comprimés', 4.20, 220, 0, 25, false, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400');

-- Catégorie 2: Anti-inflammatoires
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Étodolac 400mg', 2, 'Boîte de 14 comprimés', 12.50, 110, 0, 15, false, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=400'),
('Flurbiprofène 100mg', 2, 'Boîte de 30 comprimés', 10.80, 130, 0, 16, false, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400');

-- Catégorie 3: Antibiotiques (2 médicaments indisponbibles)
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Lévofloxacine 500mg', 3, 'Boîte de 7 comprimés', 15.80, 160, 0, 18, true, 'https://images.unsplash.com/photo-1628771065518-0d82f1938462?w=400'),
('Clindamycine 300mg', 3, 'Boîte de 16 gélules', 13.20, 140, 0, 16, true, 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400');


-- Insertion des dispensaires
INSERT INTO DISPENSAIRE (CODE, NOM, TELEPHONE, RUE, VILLE, REGION, CODE_POSTAL, PAYS) VALUES
(DEFAULT, 'Dispensaire Central de Dakar', '+221 33 821 12 34', 'Avenue Léopold Sédar Senghor', 'Dakar', 'Dakar', '14000', 'Sénégal'), -- code : 1
(DEFAULT, 'Dispensaire de Thiès', '+221 33 951 45 67', 'Route Nationale 2', 'Thiès', 'Thiès', '14200', 'Sénégal'), -- code : 2
(DEFAULT, 'Dispensaire de Saint-Louis', '+221 33 961 78 90', 'Rue Blaise Diagne', 'Saint-Louis', 'Saint-Louis', '14400', 'Sénégal'), -- code : 3
(DEFAULT, 'Dispensaire de Kaolack', '+221 33 941 23 45', 'Avenue Valdiodio Ndiaye', 'Kaolack', 'Kaolack', '14800', 'Sénégal'), -- code : 4
(DEFAULT, 'Dispensaire de Ziguinchor', '+221 33 991 67 89', 'Boulevard du Général de Gaulle', 'Ziguinchor', 'Ziguinchor', '15000', 'Sénégal'), -- code : 5
(DEFAULT, 'Dispensaire de Rufisque', '+221 33 836 12 34', 'Avenue Blaise Diagne', 'Rufisque', 'Dakar', '14100', 'Sénégal'); -- code : 6


-- Insertion des commandes
INSERT INTO COMMANDE (NUMERO, DATE_SAISIE, DATE_EXPEDITION, DISPENSAIRE_CODE) VALUES
(DEFAULT, '2024-01-10', '2024-01-12', 1), -- numero : 1
(DEFAULT, '2024-01-15', '2024-01-17', 2), -- numero : 2
(DEFAULT, '2024-02-01', '2024-02-03', 3), -- numero : 3
(DEFAULT, '2024-02-10', NULL, 1), -- numero : 4 (non expédiée)
(DEFAULT, '2024-02-15', NULL, 4), -- numero : 5 (non expédiée)
(DEFAULT, '2024-03-01', '2024-03-03', 5), -- numero : 6
(DEFAULT, '2024-03-10', NULL, 2), -- numero : 7 (non expédiée)
(DEFAULT, '2024-03-15', '2024-03-18', 6); -- numero : 8


-- Insertion des lignes de commande
-- Commande 1 (Dispensaire de Dakar)
INSERT INTO LIGNE_COMMANDE (NUMERO_COMMANDE, REFERENCE_MEDICAMENT, QUANTITE) VALUES
(1, 1, 10), -- Morphine 10mg
(1, 2, 20), -- Doliprane Effervescent 1g
(1, 4, 15); -- Étodolac 400mg

-- Commande 2 (Dispensaire de Thiès)
INSERT INTO LIGNE_COMMANDE (NUMERO_COMMANDE, REFERENCE_MEDICAMENT, QUANTITE) VALUES
(2, 2, 30), -- Doliprane Effervescent 1g
(2, 3, 25), -- Efferalgan Vitamine C
(2, 5, 10); -- Flurbiprofène 100mg

-- Commande 3 (Dispensaire de Saint-Louis)
INSERT INTO LIGNE_COMMANDE (NUMERO_COMMANDE, REFERENCE_MEDICAMENT, QUANTITE) VALUES
(3, 1, 5), -- Morphine 10mg
(3, 4, 20); -- Étodolac 400mg

-- Commande 4 (Dispensaire de Dakar - non expédiée)
INSERT INTO LIGNE_COMMANDE (NUMERO_COMMANDE, REFERENCE_MEDICAMENT, QUANTITE) VALUES
(4, 2, 50), -- Doliprane Effervescent 1g
(4, 3, 40), -- Efferalgan Vitamine C
(4, 5, 30); -- Flurbiprofène 100mg

-- Commande 5 (Dispensaire de Kaolack - non expédiée)
INSERT INTO LIGNE_COMMANDE (NUMERO_COMMANDE, REFERENCE_MEDICAMENT, QUANTITE) VALUES
(5, 1, 8), -- Morphine 10mg
(5, 2, 15); -- Doliprane Effervescent 1g

-- Commande 6 (Dispensaire de Ziguinchor)
INSERT INTO LIGNE_COMMANDE (NUMERO_COMMANDE, REFERENCE_MEDICAMENT, QUANTITE) VALUES
(6, 3, 35), -- Efferalgan Vitamine C
(6, 4, 25), -- Étodolac 400mg
(6, 5, 20); -- Flurbiprofène 100mg

-- Commande 7 (Dispensaire de Thiès - non expédiée)
INSERT INTO LIGNE_COMMANDE (NUMERO_COMMANDE, REFERENCE_MEDICAMENT, QUANTITE) VALUES
(7, 1, 12), -- Morphine 10mg
(7, 2, 18); -- Doliprane Effervescent 1g

-- Commande 8 (Dispensaire de Rufisque)
INSERT INTO LIGNE_COMMANDE (NUMERO_COMMANDE, REFERENCE_MEDICAMENT, QUANTITE) VALUES
(8, 2, 25), -- Doliprane Effervescent 1g
(8, 3, 30), -- Efferalgan Vitamine C
(8, 4, 15), -- Étodolac 400mg
(8, 5, 10); -- Flurbiprofène 100mg
