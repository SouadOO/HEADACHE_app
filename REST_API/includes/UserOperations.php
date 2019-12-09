<?php 

    class UserOperations{
		
        private $con; 
		
        function __construct($connexion){
			require_once 'PassHash.php';
            $this->con = $connexion; 
        }
		
		/**
		 * Checking for duplicate user by email address
		 */
        private function isUserExist($email){
            $stmt = $this->con->prepare("SELECT user_id FROM users WHERE email = ? ;");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
            $stmt->store_result(); 
            return $stmt->num_rows > 0;  
        }
		
		
		/**
		 * Fetching User password by email
		 * @param String $email User login email id
		 * @return $password User login password
		 */
        private function getUsersPasswordByEmail($email){
            $stmt = $this->con->prepare("SELECT password_hash FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
            $stmt->bind_result($password);
            $stmt->fetch(); 
            return $password; 
        }
		
		
		


		/**
		 * Fetching user by email
		 * @param String $api_key user api key
		 */
		public function getUserByEmail($email){
			//extract user info
            $stmt = $this->con->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
            $stmt->bind_result($id, $email,$pass, $user_type, $name, $sex, $age, $my_doctor_id,$created_at);
            $stmt->fetch(); 
			$user = array(); 
            $user['id'] = $id; 
            $user['email']=$email; 
            $user['user_type'] = $user_type;
			$user['name'] = $name;
			$user['created_at'] = $created_at;			
			
			//extract patient info
			if ($user_type=="P"){
				$user['sex']=$sex; 
				$user['age']=$age; 
				$user['my_doctor_id'] = $my_doctor_id;
			}
			$stmt->close(); 
            return $user; 
        }


		/**
		 * Creating new Patient
		 */
        public function createUser($email, $password,$user_type, $name,$sex=null, $age=null, $my_doctor_id=null){
		   
           // First check if patient already existed in db
		   if(!$this->isUserExist($email)){
			   
			  // Generating password hash
				$password_hash = PassHash::hash($password);
				
			  //insert new user in database 
			  if($user_type=="P"){
				  $stmt = $this->con->prepare(" INSERT INTO users(email, password_hash,user_type,name,sex,age,my_doctor_id) VALUES (?, ?, ?, ?, ?, ?, ?)");	
                  $stmt->bind_param("sssssii", $email, $password_hash, $user_type,$name, $sex, $age, $my_doctor_id);				  
			  }else{
				  $stmt = $this->con->prepare(" INSERT INTO users(email, password_hash,user_type,name) VALUES (?, ?, ?, ?)");	
                  $stmt->bind_param("ssss", $email, $password_hash, $user_type,$name);	
			  }
                
				//user inserted
                if($stmt->execute())
					return USER_CREATED;
				//user not inserted	
                return USER_FAILURE;
           }
		   //user exist
           return USER_EXISTS;
        }
		
		
		/**
		 * Checking User login
		 * @param String $email User login email id
		 * @param String $password User login password
		 * @return boolean User login status success/fail
		 */
		
        public function checkLogin($email, $password){
			
            if($this->isUserExist($email)){
				
			  // Found password of user with the email
                $hashed_password = $this->getUsersPasswordByEmail($email);
				
			  // Now verify the password
				if (PassHash::check_password($hashed_password, $password)) {
					// User password is correct
					return USER_AUTHENTICATED;
					
				} else {
                    // user password is incorrect
					return USER_PASSWORD_DO_NOT_MATCH;
            }
            }else{
				// user not existed with the email
                return USER_NOT_FOUND; 
            }
        }	
		
		/**
		 * Fetching patients of a doctor by id
		 * @param String $doctor_id doctor id
		 */
		public function getPatientOfDoctor($id) {
			$stmt = $this->con->prepare("SELECT * FROM users WHERE my_doctor_id = ?");
			$stmt->bind_param("i", $id);
			$stmt->execute(); 
			$stmt->bind_result($id_p, $email, $p, $t, $name, $sex, $age, $doc, $created_at);
			
            $patients = array(); 
            while($stmt->fetch()){ 
				$patient = array(); 
                $patient['id'] = $id_p; 
				$patient['email'] = $email;
                $patient['name']=$name; 
                $patient['sex'] = $sex; 
                $patient['age'] = $age; 
				$patient['created_at'] = $created_at; 
                array_push($patients, $patient);
				$stmt->bind_result($id_p, $email, $p, $t, $name, $sex, $age, $doc, $created_at);
                
            }             
            return $patients; 
		}
		
		
		/**
		 * Fetching patient type by email
		 * @param String $patient_id patient id
		 */
		public function getPatientById($patient_id){
            $stmt = $this->con->prepare("SELECT email,name,sex,my_doctor_id FROM users WHERE user_id = ? ;");
            $stmt->bind_param("i", $patient_id);
            $stmt->execute(); 
            $stmt->bind_result($email,$name,$sex,$my_doctor_id);
			$stmt->fetch();
			$patient=array();
			$patient["id"]=$patient_id;
			$patient["email"]=$email;
			$patient["name"]=$name;
			$patient["sex"]=$sex;
			$patient["my_doctor_id"]=$my_doctor_id;
            return $patient; 
        }
		
		
		/**
		 * Fetching all doctors in database
		 * @param String $patient_id patient id
		 */
		public function getAlldoctors(){
            $stmt = $this->con->prepare("SELECT user_id,email,name FROM users WHERE user_type = 'D' ;");
            $stmt->execute(); 
			$stmt->bind_result($id,$email,$name);
			
            $doctors = array(); 
			while($stmt->fetch()){
				$doctor=array();
				$doctor["id"]=$id;
				$doctor["email"]=$email;
				$doctor["name"]=$name;
				array_push($doctors,$doctor);
				$stmt->bind_result($id,$email,$name);
				
			}
			return $doctors; 
        }
		
	
		/**
		 * Updating Patient fields
		 * @param String $sex user's sex Male or female
		 * @param String $age user's age
		 * @param String $doctor_id user's doctor id
		 * @param String $id id of user
		 */
		
        public function updatePatient($email,$name, $sex, $age, $my_doctor_id, $id){
			$stmt = $this->con->prepare("UPDATE users SET email=?, name=?, sex=?, age=?, my_doctor_id = ? WHERE user_id = ?");
			$stmt->bind_param("sssiii", $email,$name,$sex,$age,$my_doctor_id, $id);
			if($stmt->execute())
					return true; 
			return false; 
        }
		
		
		/**
		 * Updating User password
		 * @param String $email 
		 * @param String $password
		 */
		
        public function updatePassword($email,$password){
			if($this->isUserExist($email)){
				// Generating password hash
				$password_hash = PassHash::hash($password);
					
				$stmt = $this->con->prepare("UPDATE users SET password_hash= ? WHERE email = ?");
				$stmt->bind_param("ss", $password_hash, $email);
				if($stmt->execute())
						return USER_CREATED; 
				return USER_FAILURE; 
			}
			return USER_EXISTS;
        }
		
		
		/**
		 * Updating Doctor's fields
		 * @param String $name doctor's full name
		 */
		
        public function updateDoctor($email,$name, $id){
			$stmt = $this->con->prepare("UPDATE users SET name=?, email=? WHERE user_id = ?");
			$stmt->bind_param("ssi", $name,$email,$id);
			if($stmt->execute())
				return true; 
			return false; 
        }
		
		
		/**
		 * Deleting a User
		 * @param String $user_id id of the User to delete
		 */
		public function deleteUser($id){
            $stmt = $this->con->prepare("DELETE FROM users WHERE user_id = ?");
            $stmt->bind_param("i", $id);
            if($stmt->execute())
				if (deleteUser($id))
					return true; 
            return false; 
        }
	}