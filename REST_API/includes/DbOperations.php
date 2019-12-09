<?php 

class DbOperations{

        private $con;
		public $user;
		public $code;
		public $attack;

        function __construct(){
			
            require_once dirname(__FILE__) . '/DbConnect.php';
			require_once dirname(__FILE__) . '/UserOperations.php';
			require_once dirname(__FILE__) . '/CodeOperations.php';
			require_once dirname(__FILE__) . '/AttacksOperations.php';
			
            $db = new DbConnect; 
            $this->con = $db->connect(); 
			$this->code = new CodeOperations($this->con); 
			$this->user = new UserOperations($this->con); 
			$this->attack =  new AttacksOperations($this->con); 
        }
		
		
		//-------------------------Symptoms operations---------------------
	
		/**
		 * Creating new Symptom
		 * @param String $name symptom name
		 * @param String $icon nbr 
		 */
		 
        function createSymptom($name, $icon){
		    
			if (!isSymptomExist($name)){
				$stmt = $this->con->prepare("  INSERT INTO Symptoms (name, icon) VALUES (?, ?)");
                $stmt->bind_param("ss",$name, $icon);
				if($stmt->execute() ){
					return FIELD_CREATED; 
				return FIELD_FAILURE; 
			}
			return FIELD_EXIST;
            }
        }
		
		/**
		 * Get all Symptoms of a patient
		 * @param String $patient_id 
		 */
		 
        function getSymptomsOfPatient($patient_id){
		    
			$stmt = $this->conn->prepare("SELECT * FROM Symptoms WHERE (patient_id = ? OR patient_id=NULL)");
			$stmt->bind_param("i", $patient_id);
			$stmt->execute(); 
            $stmt->bind_result($id, $name, $icon);
            $symptoms = array(); 
            while($stmt->fetch()){ 
                $symptom = array(); 
                $symptom['id'] = $id; 
                $symptom['name']=$name; 
                $symptom['icon'] = $icon; 
                array_push($symptoms, $symptom);
            }             
            return $symptoms;
             
        }
		
		
		/**
		 * Get Symptoms by id
		 * @param String $id of symptom 
		 */
		 
        function getSymptomsById($id){
		    
			$stmt1 = $this->conn->prepare("SELECT * FROM Symptoms WHERE id = ? ");
			$stmt1->bind_param("i", $id);
			$stmt1->execute(); 
            $stmt1->bind_result($symptom_id);
			$stmt->fetch();
            $symptom = array(); 
            $symptom['id'] = $id; 
            $symptom['name']=$name; 
            $symptom['icon'] = $icon;
            return $symptom;
             
        }
		
		/**
		 * Checking for symptoms  by name
		 * @param String $name 
		 */
        function isSymptomExist($name){
            $stmt = $this->con->prepare("SELECT id FROM Symptoms WHERE name = ?");
            $stmt->bind_param("s", $name);
            $stmt->execute(); 
            $stmt->store_result(); 
            return $stmt->num_rows > 0;  
        }
		
		//-------------------------------Attack_Symptom operations
		
		/**
		 * Link Symptom to one attack
		 * @param String $attack_id 
		 * @param String $symptom_id symptom id
		 */
		 
        function CreateSymptomsAttack($attack_id,$symptom_id){
		    
			$stmt1 = $this->conn->prepare("INSERT INTO Attack_Symptom (attack_id, symptom_id) VALUES (?, ?) ");
			$stmt1->bind_param("i", $attack_id,$symptom_id);
			if ($stmt1->execute())
				return true;
			return false;
             
        }
		
		
		/**
		 * UnLink Symptom to one attack
		 * @param String $attack_id 
		 * @param String $symptom_id symptom id
		 */
		 
        function DeleteSymptomsAttack($attack_id,$symptom_id){
		    
			$stmt1 = $this->conn->prepare("DELETE FROM doctors WHERE attack_id = ? AND symptom_id=? ");
			$stmt1->bind_param("i", $attack_id,$symptom_id);
			if ($stmt1->execute())
				return true;
			return false;
             
        }
		
		/**
		 * Get Symptoms of an attack
		 * @param String $attack_id 
		 */
		 
        function getSymptomsOfAttack($attack_id){
		    
			$stmt1 = $this->conn->prepare("SELECT * FROM Attack_Symptom WHERE attack_id = ? ");
			$stmt1->bind_param("i", $attack_id);
			$stmt1->execute(); 
            $stmt1->bind_result($symptom_id);
            $symptoms = array(); 
            while($stmt->fetch()){ 
				$symptom=$this->getSymptomsById($symptom_id); 
                array_push($symptoms, $symptom);
            }             
            return $symptoms;
             
        }
		
		/**
		 * Update Symptoms of an attack
		 * @param String $attack_id 
		 * @param String $symptoms_id a list of id 
		 */
		 
        function UpdateSymptomsOfAttack($attack_id,$new_symptoms){
		    
			$old_symptoms=getSymptomsOfAttack($attack_id);
			$symptoms_to_delete=array_diff($old_symptoms, $new_symptoms);
			$symptoms_to_add=array_diff($new_symptoms, $old_symptoms);
			
			foreach($sym as $symptoms_to_delete){
				if(!DeleteSymptomsAttack($attack_id,$sym))
					return false;
			}
			
			foreach($sym as $symptoms_to_add){
				if(!CreateSymptomsAttack($attack_id,$sym))
					return false;
			}     
            return true;
             
        }
	
}