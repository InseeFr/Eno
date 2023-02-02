# Couverture fonctionnelle "composant de questionnaires" Lunatic - xforms - fo 

(doc reprise de Eno v2)

 |  Pogues/Eno |  Lib Lunatic |  fo | xforms | 
 |----------|----------|----------|----------|
 |  **Questions ouvertes ou réponses simples** |   |   |   |  
 |  Texte : si longueur < 250 ou non décrite |  Input |  ligne encadrée |  zone fixe  |  
 |  Texte : si longueur >= 250 |  TextArea |  bloc hauteur 5 lignes, paramétrable |  zone de dimension variable |  
 |  numérique (bornes maximum et minimum, nombre de décimales, unités) |  InputNumber *saisie de nb négatifs impossible* |  zone de saisie fixe avec ou sans précasage, unités | zone fixe, unités |  
 |  date (au format AAAA-MM-JJ) |  Datepicker  |  précasage | zone de saisie avec calendrier |  
 |  date (au format AAAA-MM ou AAAA) |  absent  |  précasage | 2 zones de saisie |  
 |  duree  |  absent  |  zone de saisie | zone de saisie |  
 |  **Questions à choix unique** |   |   |   |  
 |  booléen (pour ce dernier type de réponse, seule la case à cocher est proposée comme mise en forme) |  CheckboxBoolean |  case |  case à cocher |  
 |  Liste déroulante |  Dropdown |  ligne encadrée  |  liste déroulante |  
 |  Case à cocher décochable |  ChekboxOne |  cases à cocher |  cases à cocher  |  
 |  Case à cocher |  Radio |  cases à cocher | cases à cocher rondes |  
 |  Autocomplétion (non spécifiable dans Pogues) |  Suggester | non développé  | non développé   |  
 |  **Questions à choix multiple**|   |   |   |  
 |  Cases à cocher (décochables) |  CheckboxGroup |   cases à cocher |  cases à cocher |  
 |  Batteries de question |  Table ??? |  cases à cocher  | cases à cocher |  
 | **Autres**  |   |   |   |  
 |  Question "Autres -> préciser" |  A CREER |  encadré libre | affichage dynamique d’une question  |  
 |  Déclarations  |  Declarations  |  texte |  texte |  
 |  Infobulles |  Tooltip |  représentée par une étoile |  point interrogation, survolable |  
 |  Séquence |  Sequence |  Texte |  Texte  |  
 |  Sous-séquence |  Subsequence |  Texte  |  Texte  |  
 |  Fil d’Ariane |  Breadcrumb |  Non  |  Non et pas prévu |  
 |  Bouton |  Button |  Sans objet |  Bouton |  
 |  Barre avancement |  ProgressBar  | Sans objet  | Barre avancement  |  
 |   |   |   |   |  
 |   |   |   |   |  
 |  **Tableaux** | à tester rosterForLoop ? Roster ?  | tableaux, nb de lignes paramétrables   |  tableaux, nb de lignes fixes ou dynamiques  |  
 |  **Boucles** | Loop, LoopConstructor, rosterForLoop, DeeperLoop |  tableaux, nb de lignes paramétrables |  A demeler |  
  |   |   |   |   |  
  |  **Éléments dynamiques** |   |   |   |  
 |  Contrôles de cohérence  |  ok sauf dans boucles |  sans objet |  ok |  
 |  Contrôles de format  |  ok |  sans objet |  ok |  
 |  les filtres |  ok |  affichage d’un texte alternatif |  ok |  
 |  les redirections |  ok |  affichage d’un texte alternatif |  ok |   
 |   |   |   |   |  
 |  **Non couvert et à dev** |   |   |   |  
 |  Composant multipage  : à spécifier dans Pogues |  A CREER | A CREER  | ne sera pas fait  |  
 |  Éléments dynamiques : la personnalisation |  Ko ? |  ok | ok  |  

