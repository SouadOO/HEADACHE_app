<?php 

class CodeOperations{
		
        private $con;
		
        function __construct($connexion){
            $this->con = $connexion;		
        }
		
		
		/**
		 * Deleting a VerificationCode
		 * @param String $id id of the VerificationCode to delete
		 */
		private function deleteVerificationCode($id){
            $stmt = $this->con->prepare("DELETE FROM VerificationCode WHERE id = ?");
            $stmt->bind_param("i", $id);
            if($stmt->execute())
				if (deleteUser($id))
					return true; 
            return false; 
        }
		
		/*
		 *Create a trigger to delete code after experation day
		 *@param String $experationByDay :how many days to
		 *@param String $id the id of verificationCode
		 */
		private function addTrigger($id,$experationByDay){
			//create trrigers to delete field after $experationByDay day
			$stmt = $this->con->prepare("  DELIMITER $$
											CREATE EVENT event?
											ON SCHEDULE EVERY ? DAY
											DO
												BEGIN
													DELETE FROM VerificationCode
													WHERE id = ?;
											END$$
											DELIMITER ;
						");
			$stmt->bind_param("ssi",$id,$experationByDay,$id );
			if($stmt->execute())
				return true;
			return false;
		}
		
		
		/*
		 *Generate random string
		 *@param String $length length of string
		 */
		private function generateRandomString($length = 15) {
			$characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
			$charactersLength = strlen($characters);
			$randomString = '';
			for ($i = 0; $i < $length; $i++) {
				$randomString .= $characters[rand(0, $charactersLength - 1)];
			}
			return $randomString;
		}
		
		/*
		 *Create new VerificationCode
		 *@param String $experationByDay :how many days to
		 */
		public function CreateVerificationCode($experationByDay) {
			
			for ($i = 1; $i <= 10; $i++) {
				$code=generateRandomString();
				if (!CodeExist($code)){
					//insert new doctor in database 
					$stmt = $this->con->prepare("  INSERT INTO VerificationCode (code, experationByDay) VALUES (?, ?, ?)");
					$stmt->bind_param("si",$code,$experationByDay );
					
					if($stmt->execute() ){
						if(!($this->addTrigger($id,$experationByDay)))
						     $this->deleteVerificationCode($id);
						else
							return CODE_CREATED; 
					}
				}
			}
			return CODE_FAILURE;
		}
		
		/*
		 *Check if the given code is correct
		 *@param String $code the given code for doctor registration
		*/
		
		public  function isCodeCorrect($code){
			$stmt = $this->con->prepare("SELECT id FROM verificationCode WHERE code = ? ;");
            $stmt->bind_param("s", $code);
            $stmt->execute(); 
            $stmt->store_result(); 
            return $stmt->num_rows > 0; 	
		}
	}
		
?>