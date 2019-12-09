<?php 


class Attack_Fields{
		
		private $con;
		private $FIELD_RELATION_TABLE;
		private $FIELD_ID;
		
        function __construct($connexion,$field_table,$field_id){
            $this->con = $connexion;
			$this->FIELD_RELATION_TABLE=$field_table;
			$this->FIELD_ID=$field_id;
        }
		
		
		/**
		 * Get Fields of an attack
		 * @param String $attack_id 
		 */
		 
        function getFieldOfAttack($attack_id){
			
			$sql1="SELECT ".$this->FIELD_ID." FROM ". $this->FIELD_RELATION_TABLE." WHERE attack_id = ? ;";
			
			$stmt1 = $this->con->prepare($sql1);
			$stmt1->bind_param("i",$attack_id);
			$stmt1->execute(); 
            $stmt1->bind_result($f_id);
            $id_s = array(); 
			
			
			while($stmt1->fetch()){ 
				array_push($id_s, $f_id);
				$stmt1->bind_result($f_id);
			}
			
			$fields = array(); 
			
			
			foreach ($id_s as $id){ 
			    $field=$this->getSymptomsById($id); 
				array_push($fields, $field);
			} 

            return $fields;
             
        }
		
		
		/**
		* Get field name 
		*
		*/
		function getSymptomsById($id){
			
			$table_name=strtolower(explode("_",$this->FIELD_RELATION_TABLE)[1]);
			$sql_="SELECT name FROM ".$table_name." WHERE id = ? ;";
			
			$stmt = $this->con->prepare($sql_);
			
			$stmt->bind_param("i",$id);
			$stmt->execute(); 
			
            $stmt->bind_result($name);
			$stmt->fetch();
	
			return $name;
		}	
		
		/**
		* Get field id 
		*
		*/
		function getSymptomsByName($name){
			
			$table_name=strtolower(explode("_",$this->FIELD_RELATION_TABLE)[1]);
			
			echo $table_name;
			
			$sql_="SELECT id FROM ".$table_name." WHERE name = ? ;";
			
			$stmt = $this->con->prepare($sql_);
			
			$stmt->bind_param("s",$name);
			$stmt->execute(); 
			
            $stmt->bind_result($id);
			$stmt->fetch();
	
			return $id;
		}	
		
	
		/**
		 * Link fields to one attack
		 * @param String $attack_id 
		 * @param String $field_id field id
		 */
        public function CreateFieldsOfAttack($attack_id,$fields){
			
			
			$arr=(gettype($fields)=="array")?$fields:json_decode($fields,true);
			
			foreach ($arr as $name){
				$table_name=strtolower($this->FIELD_RELATION_TABLE);
				$sql_="INSERT INTO ".$table_name." (attack_id, ".$this->FIELD_ID.") VALUES (?, ?) ;";
				
				
				$id=$this->getSymptomsByName($name);
				$stmt = $this->con->prepare($sql_);
				$stmt->bind_param("ii",$attack_id,$id);
				
				$stmt->execute(); 
		    }
			return true;
        }
		
		
		/**
		 * UnLink Fields to one attack
		 * @param String $attack_id 
		 * @param String $field_id field id
		 */
        function DeleteFieldsOfAttack($attack_id){
			$table_name=strtolower($this->FIELD_RELATION_TABLE);
			$sql_="DELETE FROM ".$table_name." WHERE attack_id = ? ;";
		
			$stmt = $this->con->prepare($sql_);
			$stmt->bind_param("i",$attack_id);
			
			return $stmt->execute(); 
        }
		
	
		
		/**
		 * Update Fields of an attack
		 * @param String $attack_id 
		 * @param String $new_fields an array of new  field
		 */
		 
        function UpdateFieldOfAttack($attack_id,$new_fields){
		    
			$old_fields=getFieldOfAttack($attack_id);
			$fields_to_delete=array_diff($old_fields, $new_fields);
			$fields_to_add=array_diff($new_fields, $old_fields);
			
			foreach($f as $fields_to_delete){
				if(!DeleteFieldAttack($attack_id,$f))
					return false;
			}
			
			foreach($f as $fields_to_add){
				if(!CreateFieldAttack($attack_id,$sym))
					return false;
			}     
            return true;
             
        }
		
	     
}



class AttacksOperations{
		
        private $con;
		private $sym;
		private $aura;
		private $trig;
		private $meddi;
		private $posi;
		
		
        function __construct($connexion){
			
			define('SYMPTOM', 'Attack_Symptoms');
			define('AURA', 'Attack_AuraSymptoms');
			define('TRIGGER', 'Attack_Triggers');
			define('MEDDICATION', 'Attack_Meddications');
			define('POSITION', 'Attack_Positions');
			
			define('SYMPTOM_ID', 'symptom_id');
			define('AURA_ID', 'aura_id');
			define('TRIGGER_ID', 'trigers_id');
			define('MEDDICATION_ID', 'meddication_id');
			define('POSITION_ID', 'position_id');
		
            $this->con   = $connexion;
			$this->sym  = new Attack_Fields($this->con,SYMPTOM,SYMPTOM_ID);
			$this->aura = new Attack_Fields($this->con,AURA,AURA_ID);
			$this->trig = new Attack_Fields($this->con,TRIGGER,TRIGGER_ID);
			$this->meddi= new Attack_Fields($this->con,MEDDICATION,MEDDICATION_ID);
			$this->posi= new Attack_Fields($this->con,POSITION,POSITION_ID);
        }
		
		
		/**
		 * Fetching attacks of a patient
		 * @param String $patient_id doctor id
		 */
		public function getAttacksOfPatient($patient_id) {
			$stmt = $this->con->prepare("SELECT * FROM attacks WHERE patient_id = ?;");
			$stmt->bind_param("i", $patient_id);
			$stmt->execute(); 
            $stmt->bind_result($attack_id, $status, $duration, $intensity, $patient_id, $note, $created_at, $started_at, $stopped_at);
            $attacks = array(); 
            while($stmt->fetch()){ 
                $attack = array(); 
                $attack['attack_id'] = $attack_id; 
                $attack['status']=$status; 
                $attack['duration'] = $duration; 
                $attack['intensity'] = $intensity; 
				$attack['notes'] = $note; 
				$attack['patient_id'] = $patient_id; 
				$attack['started_at'] = $started_at;
				$attack['stopped_at'] = $stopped_at;
				array_push($attacks, $attack);
            }
			
			$finale_attacks=array();
			foreach($attacks as $a){
				$a['symptoms']=$this->sym->getFieldOfAttack($a["attack_id"]);
				$a['auraSymptoms']=$this->aura->getFieldOfAttack($a["attack_id"]);
				$a['triggers']=$this->trig->getFieldOfAttack($a["attack_id"]);
				$a['medications']=$this->meddi->getFieldOfAttack($a["attack_id"]);
				$a['positions']=$this->posi->getFieldOfAttack($a["attack_id"]);
				array_push($finale_attacks, $a);
			}
			
            return $finale_attacks; 
		}
		
		
		
		/**
		 * Update notes of an attack 
		 * @param String $attack_id attack id
		 * @param String $notes new notes
		 */
		public function UpdateAttackNote($attack_id,$notes) {
			$stmt = $this->con->prepare("UPDATE attacks SET notes=? WHERE attack_id = ? ;");
			$stmt->bind_param("si", $notes, $attack_id);
			return $stmt->execute();
		}
		
		/**
		 * Update intensity of an attack 
		 * @param String $attack_id attack id
		 * @param String $intensity
		 */
		public function UpdateAttackIntensity($attack_id,$intensity) {
			$stmt = $this->con->prepare("UPDATE attacks SET intensity=? WHERE attack_id = ? ;");
			$stmt->bind_param("si", $intensity, $attack_id);
			return $stmt->execute();
		}
		
		
		/**
		 * Update dates of an attack 
		 * @param String $attack_id attack id
		 * @param String $status 
		 * @param String $duration 
		 * @param String $started_at 
		 * @param String $stopped_at 
		 */
		public function UpdateAttackDates($attack_id,$status,$duration,$started_at,$stopped_at) {
			$stmt = $this->con->prepare("UPDATE attacks SET status=?, duration=?, started_at=?,stopped_at=?  WHERE attack_id = ? ;");
			$stmt->bind_param("ssssi",$status,$duration,$started_at,$stopped_at,$attack_id);
			return $stmt->execute();
		}
			
			
		/**
		 * Update dates of an attack 
		 * @param String $attack_id attack id
		 * @param String $status 
		 * @param String $duration 
		 * @param String $started_at 
		 * @param String $stopped_at 
		 */
		public function updateAttackField($attack_id,$fields,$field_name) {
			switch ($field_name){
				case "symptoms":
					//delete all symptoms of this attack
					$results=$this->sym->DeleteFieldsOfAttack($attack_id);
					//add new symptoms to this attack
					$results=$this->sym->CreateFieldsOfAttack($attack_id,$fields);
					break;
				case "aura":
					//delete all symptoms of this attack
					$results=$this->aura->DeleteFieldsOfAttack($attack_id);
					//add new symptoms to this attack
					$results=$this->aura->CreateFieldsOfAttack($attack_id,$fields);
					break;
				case "triggers":
					//delete all symptoms of this attack
					$results=$this->trig->DeleteFieldsOfAttack($attack_id);
					//add new symptoms to this attack
					$results=$this->trig->CreateFieldsOfAttack($attack_id,$fields);
					break;
				case "medications":
					//delete all symptoms of this attack
					$results=$this->meddi->DeleteFieldsOfAttack($attack_id);
					//add new symptoms to this attack
					$results=$this->meddi->CreateFieldsOfAttack($attack_id,$fields);
					break;
				case "positions":
					//delete all symptoms of this attack
					$results=$this->posi->DeleteFieldsOfAttack($attack_id);
					//add new symptoms to this attack
					$results=$this->posi->CreateFieldsOfAttack($attack_id,$fields);
					break;
			}
			return $results;
		}
		
		/**
		 * Fetching attack's status 
		 * @param String $id attack's id
		 */
		private function IsInProgress($id){
			$stmt = $this->con->prepare("SELECT status FROM attacks WHERE attack_id = ?");
			$stmt->bind_param("i", $attack_id);
			$stmt->bind_result($status);
            $stmt->fetch(); 
			return ($status=="in_progress");
        }
		
		/**
		 * Creating new Attack
		 * @param String $patient_id patient's id the owner of this attack
		 * @param String $started_at timestamp 
		 * @param String $intensity Ddegree of attack
		 * @param String $duration duration of attack if it is stopped
		 * @param String $stopped_at timestamp
		 */
		 
        public function createAttack($status ,$duration ,$intensity ,$patient_id ,$started_at ,$stopped_at,$notes,$symptoms ,$auraSymptoms ,$triggers ,$medications ,$positions ){
		    
			$stmt = $this->con->prepare("INSERT INTO attacks (status,duration,intensity,patient_id,started_at,stopped_at,notes) VALUES (?, ?, ?, ?, ?, ?, ?);");
			
			$duration	=(int)$duration;
			$patient_id	=(int )$patient_id;
			
            $stmt->bind_param("sisisss",$status,$duration, $intensity,$patient_id, $started_at,$stopped_at,$notes);
			
			$stmt->execute();
			
			$attack_id=$stmt->insert_id;
			
			$this->sym->CreateFieldsOfAttack($attack_id,$symptoms);
			$this->aura->CreateFieldsOfAttack($attack_id,$auraSymptoms);
			$this->trig->CreateFieldsOfAttack($attack_id,$triggers);
			$this->meddi->CreateFieldsOfAttack($attack_id,$medications);
			$this->posi->CreateFieldsOfAttack($attack_id,$positions);
			
            return true;
        
		}
		
		
		/**
		 * Stop an Attack
		 * @param String $duration duration of attack
		 * @param String $stopped_at timestamp
		  * @param String $id the id of attack
		 */
		
        public function stopAttack($duration,$stopped_at, $attack_id){
			if (IsInProgress($id)){
				$status="in_progress";
				$stmt = $this->con->prepare("UPDATE attacks SET status=? duration=? stopped_at=? WHERE attack_id = ?");
				$stmt->bind_param("siii", $status,$duration, $stopped_at, $attack_id);
				if($stmt->execute())
					return STOP_SUCESS; 
				return STOP_FAILURE; 
			}else{
				return WAS_IN_STOP;
			}
        }
		
		
		
		/**
		 * Fetching the notes of an attacks 
		 * @param String $attack_id attack id
		 */
		public function getAttackNote($attack_id) {
			$stmt = $this->con->prepare("SELECT notes FROM attacks WHERE attack_id = ?");
			$stmt->bind_param("i", $attack_id);
			$stmt->bind_result($notes);
            $stmt->fetch(); 
			return $notes;
		}
		
		/**
		 * Fetching attack by id
		 * @param String $attack_id attack id
		 */
		public function getAttackById($attack_id,$isDoctor) {
			$stmt = $this->con->prepare("SELECT * FROM attacks WHERE attack_id = ?");
			$stmt->bind_param("i", $attack_id);
			$stmt->execute(); 
            $stmt->bind_result($attack_id, $status, $duration, $intensity. $patient_id, $notes, $created_at, $started_at, $stopped_at);
            $stmt->fetch();
			$stmt->close();
            $attack = array(); 
            $attack['attack_id'] = $attack_id; 
            $attack['status']=$status; 
            $attack['duration']   = $duration; 
            $attack['intensity']  = $intensity; 
			$attack['patient_id'] = $patient_id; 
			$attack['started_at'] = $started_at;
			$attack['stopped_at'] = $stopped_at;  
			
			//only doctor can see note
			if ($isDoctor){
				$attack['notes']= $notes;
			}
			
			//get symptoms
			$attack['symptoms']=$this->sym->getFieldOfAttack($attack_id);
			
			//get aura symptoms
			$attack['aura']=$this->aura->getFieldOfAttack($attack_id);

			//get triggers 
			$attack['triggers']=$this->trig->getFieldOfAttack($attack_id);
			
			//get medications 
			$attack['meddications']=$this->meddi->getFieldOfAttack($attack_id);
			
            return $attack; 
		}
		
		
		
}



?>