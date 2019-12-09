<?php
ob_start();


use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

require '../vendor/autoload.php';
require '../includes/DbOperations.php';

$app = new \Slim\App(['settings'=>['displayErrorDetails'=>true ]]);

$app->get('/user/hello/{name}',function (Request $request, Response $response, array $args) {
    $response_data['error']=false; 
    $response_data['message'] = 'Login Successful';
    $response_data['user']=$args['name'];
	
    $response->write(json_encode($response_data));

	return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(200);
});




/**
 * User Registration
 * url - /user/register
 * method - POST
 * params -$email, $password,$user_type, $name, $sex, $age, $my_doctor_id
 */
$app->post('/user/register', function(Request $request, Response $response){
	
	if(!haveEmptyParameters(array('email', 'password','user_type', 'name'), $request, $response)){
			
			$request_data = $request->getParsedBody(); 
			
			$email = $request_data['email'];
			$password = $request_data['password'];
			$user_type = $request_data['user_type'];
			$name = $request_data['name']; 
		
			
			$db = new DbOperations;
			
			if($user_type=="P"){
				$sex = $request_data['sex'];
				$age = $request_data['age'];
				$my_doctor_id= $request_data['my_doctor_id'];
				$result = $db->user->createUser($email, $password,$user_type, $name,$sex, $age, $my_doctor_id);
			}else{
				$result = $db->user->createUser($email, $password,$user_type, $name);
			}
			
			
			if($result == USER_CREATED){
				
				$user = $db->user->getUserByEmail($email);
				
				$answer = array(); 
				$answer['error'] = false; 
				$answer['message'] = 'User created successfully';
				$answer['user'] = $user;
				$response->write(json_encode($answer));
				return $response
							->withHeader('Content-type', 'application/json')  
							->withStatus(200);
							
			}else if($result == USER_FAILURE){

				$answer = array(); 
				$answer['error'] = true; 
				$answer['message'] = 'Some error occurred with User creation';
				$response->write(json_encode($answer));	    

			}else if($result == USER_EXISTS){
				$answer = array(); 
				$answer['error'] = true; 
				$answer['message'] = 'User Already Exists'; 
				$response->write(json_encode($answer));
			}
			
	}
	return $response
	->withHeader('Content-type', 'application/json')
	->withStatus(423);       
});


/**
 * User Login
 * url - /user/login
 * method - POST
 * params -$email, $password
 */
$app->post('/user/login', function(Request $request, Response $response){
	
    if(!haveEmptyParameters(array('email', 'password'), $request, $response)){
		
        $request_data = $request->getParsedBody(); 

        $email = $request_data['email'];
        $password = $request_data['password'];
        
        $db = new DbOperations; 
        $result = $db->user->checkLogin($email, $password);
		
        if($result == USER_AUTHENTICATED){
            
            $user = $db->user->getUserByEmail($email);
            $response_data = array();
            $response_data['error']=false; 
            $response_data['message'] = 'Login Successful';
            $response_data['user']=$user; 
            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);				

        }else if($result == USER_NOT_FOUND){
            $response_data = array();
            $response_data['error']=true; 
            $response_data['message'] = 'User not exist';
            $response->write(json_encode($response_data));

        }else if($result == USER_PASSWORD_DO_NOT_MATCH){
            $response_data = array();
            $response_data['error']=true; 
            $response_data['message'] = 'Invalid password';
            $response->write(json_encode($response_data)); 
       }
	}
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422);   	
});


/**
 * Update User password
 * url - /user/update
 * method - PUT
 */
$app->put('/user/update', function(Request $request, Response $response, array $args){

    if(!haveEmptyParameters(array('email','password'), $request, $response)){

        $request_data = $request->getParsedBody(); 
        $email = $request_data['email'];
        $password = $request_data['password'];

        $db = new DbOperations; 
		
		$result = $db->user->updatePassword($email,$password);
			

        if($result==USER_CREATED){
            $response_data = array(); 
            $response_data['error'] = false; 
            $response_data['message'] = 'Password Updated Successfully';
            $response_data['user'] = $db->user->getUserByEmail($email);

            $response->write(json_encode($response_data));

            return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
			
        }else if ($result==USER_CREATED){
            $response_data = array(); 
            $response_data['error'] = true; 
            $response_data['message'] = 'Please try again later';
            $response->write(json_encode($response_data));       
        }else if ($result==USER_EXISTS){
            $response_data = array(); 
            $response_data['error'] = true; 
            $response_data['message'] = 'Email Unfound !!';
            $response->write(json_encode($response_data));       
        }
    }
    
    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(422);  

});



/**
 * Select all doctors in database
 * url - /doctor/getAll
 * method - GET
 */
$app->get('/doctor/getAll',function (Request $request, Response $response, array $args) {
    $response_data['error']=false; 
	$response_data['message'] = 'Successful selection';
	
	$db = new DbOperations; 
    $response_data['users']=$db->user->getAlldoctors();
	
    $response->write(json_encode($response_data));

	return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(200);
});




/**
 * Update User info
 * url - /user/update/{id}
 * method - PUT
 */
$app->put('/user/update/{id}', function(Request $request, Response $response, array $args){

    $id = $args['id'];

    if(!haveEmptyParameters(array('email','name'), $request, $response)){

        $request_data = $request->getParsedBody(); 
        $email = $request_data['email'];
        $name = $request_data['name'];

        $db = new DbOperations; 
		
		if($user_type=="P"){
				if(!haveEmptyParameters(array('sex', 'age','my_doctor_id'), $request, $response)){
			
					$sex = $request_data['sex'];
					$age = $request_data['age'];
					$my_doctor_id= $request_data['my_doctor_id'];
					$result = $db->user->updatePatient($email,$name, $sex, $age, $my_doctor_id, $id);
				}
		}else{
				$result = $db->user->updateDoctor($email,$name,$id);
		}	

        if($result){
            $response_data = array(); 
            $response_data['error'] = false; 
            $response_data['message'] = 'User Updated Successfully';
            $response_data['user'] = $db->user->getUserByEmail($email);

            $response->write(json_encode($response_data));

            return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
			
        }else{
            $response_data = array(); 
            $response_data['error'] = true; 
            $response_data['message'] = 'Please try again later';
            $response->write(json_encode($response_data));       
        }
    }
    
    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(422);  

});


/**
 * delete User
 * url - /user/delete/{id}
 * method - DELETE
 */
$app->delete('/user/delete/{id}', function(Request $request, Response $response, array $args){
    $id = $args['id'];

    $db = new DbOperations; 

    $response_data = array();
	$result=$db->user->deleteUser($id);

    if($result){
        $response_data['error'] = false; 
        $response_data['message'] = 'User has been deleted';    
    }else{
        $response_data['error'] = true; 
        $response_data['message'] = 'Plase try again later';
    }

    $response->write(json_encode($response_data));
    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);
});


/**
 * Get doctor's patient
 * url - /doctor/myPatients
 * method - GET
 * params -$my_doctor_id
 */
$app->get('/doctor/myPatients/{id}', function(Request $request, Response $response, array $args){
	
	$request_data = $request->getParsedBody(); 
	$id = $args['id'];
	
    $db = new DbOperations; 

    $patients = $db->user->getPatientOfDoctor($id);

    $response_data = array();

    $response_data['error'] = false; 
	$response_data['message'] = "Successful Selection"; 
    $response_data['users'] = $patients; 

    $response->write(json_encode($response_data));
    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);  

});


/**
 * Get patient details
 * url - patient/details
 * method - GET
 * params -$patient_id
 */
$app->get('/patient/details', function(Request $request, Response $response){
	
	$request_data = $request->getParsedBody(); 
	$patient_id = $request_data['patient_id'];
	
    $db = new DbOperations; 

    $patient = $db->patient->getPatientById($patient_id);

    $response_data = array();

    $response_data['error'] = false; 
    $response_data['patient'] = $patient; 

    $response->write(json_encode($response_data));
    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);  

});


/**
 * Create new attack
 * url - patient/new_attack
 * method - post
  *params :   patient_id, started_at, intensity,duration=null,stopped_at=null 
 */

$app->post('patient/new_attack', function(Request $request, Response $response){
	
	
	if(!haveEmptyParameters(array('patient_id', 'started_at','intensity'), $request, $response)){
		$request_data = $request->getParsedBody(); 
		$patient_id   = $request_data['patient_id'];
		$started_at   = $request_data['started_at'];
		$intensity    = $request_data['intensity'];
		$duration     = $request_data['duration'];
		$stopped_at   = $request_data['stopped_at'];
		
		$db = new DbOperations; 

		$result = $db->attack->createAttack($patient_id, $started_at, $intensity,$duration,$stopped_at);
		
		if($result==ATTACK_CREATED){
			$response_data = array();
			$response_data['error'] = false; 
			$response_data['message'] = "The new attack is created successfully"; 

			$response->write(json_encode($response_data));
				return $response
				->withHeader('Content-type', 'application/json')
				->withStatus(200);  
			
		}else if($result==ATTACK_FAILURE){
			
			$response_data = array();
			$response_data['error'] = false; 
			$response_data['message'] = "Some error occurred with Attack creation"; 

			$response->write(json_encode($response_data));	
		}
	}
	return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(200);  
   
});


/**
 * Get patient list of attacks for calendar
 * url - Patient/all_attacks
 * method - GET
 * params -$patient_id
 */
$app->get('/patient/all_attacks/{id}', function(Request $request, Response $response, array $args){
	
	$request_data = $request->getParsedBody(); 
	$patient_id = $id = $args['id'];
	
    $db = new DbOperations; 

    $attacks = $db->attack->getAttacksOfPatient($patient_id);

    $response_data = array();

    $response_data['error'] = false; 
	$response_data['message'] = "Selected successfully"; 
    $response_data['users'] = $attacks; 

    $response->write(json_encode($response_data));
	
    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);  

});


/**
 * Update attack and stopped
 * url - /user/update/{id}
 * method - PUT
 */
$app->put('/patient/stop_attack/{id}', function(Request $request, Response $response, array $args){

    $attack_id = $args['id'];

    if(!haveEmptyParameters(array('duration','stopped_at'), $request, $response)){

        $request_data = $request->getParsedBody(); 
        $duration = $request_data['duration'];
        $stopped_at = $request_data['stopped_at'];

        $db = new DbOperations; 
		
		$result = $db->attack->stopAttack($duration,$stopped_at, $attack_id);

        if($result==STOP_SUCESS){
            $response_data = array(); 
            $response_data['error'] = false; 
            $response_data['message'] = 'Attack sttoped Successfully';
			
            $response->write(json_encode($response_data));
            return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
			
        }else if($result==STOP_FAILURE){
            $response_data = array(); 
            $response_data['error'] = true; 
            $response_data['message'] = 'Please try again late';
            $response->write(json_encode($response_data));       
        }else if($result==WAS_IN_STOP){
            $response_data = array(); 
            $response_data['error'] = true; 
            $response_data['message'] = 'Attack already sttoped ';
            $response->write(json_encode($response_data));       
        }
    }
    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(422);  

});


/**
 * Get attacks details
 * url - /patient/attack_details/{id}
 * method - GET
 * params -$attack_id
 * params -$IsDoctor is user a doctor or not 
 */
$app->get('/patient/attack_details/{id}', function(Request $request, Response $response){
	
	$request_data = $request->getParsedBody(); 
	$attack_id = $args['id'];
	$isDoctor = $request_data['IsDoctor'];
	
    $db = new DbOperations; 

    $attack = $db->attack->getAttackById($attack_id,$isDoctor );

    $response_data = array();

    $response_data['error'] = false; 
    $response_data['attack'] = $attack; 

    $response->write(json_encode($response_data));
    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);  

});


/**
 * add/update note to attack
 * url - /patient/updateNote/{id}
 * method - PUT
 * params -$note 
 */
$app->put('/patient/updateNote/{id}', function(Request $request, Response $response, array $args){
	
	$attack_id = $args['id'];
	$request_data = $request->getParsedBody(); 
	$notes = $request_data['note'];
	
    $db = new DbOperations; 

    $results = $db->attack->UpdateAttackNote($attack_id,$notes);
	
	if($results){
		$response_data = array();
		$response_data['error'] = false;  
		$response_data['message'] = "done!!";  
		$response->write(json_encode($response_data));
		return $response
				->withHeader('Content-type', 'application/json')
				->withStatus(200);  
	}
	return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(400); 
		
});

/**
 * add/update dates of attack
 * url - /patient/updateAttackDate/{id}
 * method - PUT
 */
$app->put('/patient/updateAttackDate/{id}', function(Request $request, Response $response, array $args){
	$attack_id 		= $args['id'];
	$request_data 	= $request->getParsedBody(); 
	$status 		= $request_data['status'];
	$duration 		= $request_data['duration'];
	$started_at 	= $request_data['started_at'];
	$stopped_at 	= $request_data['stopped_at'];
	
    $db = new DbOperations; 
    $results = $db->attack->UpdateAttackDates($attack_id,$status,$duration,$started_at,$stopped_at);
	if($results){
		$response_data = array();
		$response_data['error'] = false;  
		$response_data['message'] = "done!!";  
		$response->write(json_encode($response_data));
		return $response
				->withHeader('Content-type', 'application/json')
				->withStatus(200);  
	}
	return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(400); 
		
});



/**
 * add/update fields of attack
 * url - /patient/updateSymptoms/{id}
 * method - PUT
 */
$app->put('/patient/updateAttack/{id}', function(Request $request, Response $response, array $args){
	$attack_id 		= $args['id'];
	$request_data 	= $request->getParsedBody(); 
	$fields 		= $request_data['fields'];
	$field_name 	= $request_data['field_name'];
	
    $db = new DbOperations; 
    $results = $db->attack->updateAttackField($attack_id,$fields,$field_name);
	
	if($results){
		$response_data = array();
		$response_data['error'] = false;  
		$response_data['message'] = "done!!";  
		$response->write(json_encode($response_data));
		return $response
				->withStatus(200);  
	}
	return $response
			->withStatus(400); 
		
});

/**
 * add/update intensity of attack
 * url - /patient/updateAttackIntensity/{id}
 * method - PUT
 * params -$intensity 
 */
$app->put('/patient/updateAttackIntensity/{id}', function(Request $request, Response $response, array $args){
	$attack_id = $args['id'];
	$request_data = $request->getParsedBody(); 
	$intensity = $request_data['intensity'];
	
    $db = new DbOperations; 

    $results = $db->attack->UpdateAttackIntensity($attack_id,$intensity);
	
	if($results){
		$response_data = array();
		$response_data['error'] = false;  
		$response_data['message'] = "done!!";  
		$response->write(json_encode($response_data));
		return $response
				->withStatus(200);  
	}
	return $response
			->withStatus(400); 
		
});


/**
 * add new attack
 * url - /patient/addAttack/
 * method - PUT
 */
$app->put('/patient/addAttack', function(Request $request, Response $response){
	$data 	= $request->getParsedBody()['attack'];
	
	$someJSON = '{"name":"Jonathan Suh","gender":"male"}';
	
	//echo $data;

	// Convert JSON string to Array
	  $request_data = json_decode($data);
	//print_r($request_data);        // Dump all data of the Array
	//echo $request_data->status; // Access Array data
  
	
	$status 		= $request_data->status;
	$duration 		= $request_data->duration;
	$intensity 		= $request_data->intensity;
	$patient_id 	= $request_data->patient_id;
	$started_at 	= $request_data->started_at;
	$stopped_at		= $request_data->stopped_at;
	$notes  		= $request_data->notes;
	
	$symptoms 		= $request_data->symptoms;
	$auraSymptoms 	= $request_data->auraSymptoms;
	$triggers 		= $request_data->triggers;
	$medications 	= $request_data->medications;
	$positions 		= $request_data->positions;
	
    $db = new DbOperations; 
    $results = $db->attack->createAttack($status ,$duration ,$intensity ,$patient_id ,$started_at ,$stopped_at,$notes,$symptoms ,$auraSymptoms ,$triggers ,$medications ,$positions );
	
	
	$response_data = array();
	$response_data['error'] = false;  
	$response_data['message'] = "done!!";  
	$response->write(json_encode($response_data));
	return $response
				->withStatus(200); 
		
});


function haveEmptyParameters($required_params, $request, $response){
    $error = false; 
    $error_params = '';
    $request_params = $request->getParsedBody(); 

    foreach($required_params as $param){
        if(!isset($request_params[$param]) || strlen($request_params[$param])<=0){
            $error = true; 
            $error_params .= $param . ', ';
        }
    }

    if($error){
        $error_detail = array();
        $error_detail['error'] = true; 
        $error_detail['message'] = 'Required parameters ' . substr($error_params, 0, -2) . ' are missing or empty';
        $response->write(json_encode($error_detail));
    }
    return $error; 
}
$app->run();
ob_end_flush();
?>