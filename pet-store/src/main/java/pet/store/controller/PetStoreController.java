package pet.store.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.service.PetStoreService;

@RestController
@RequestMapping("/pet_store")
@Slf4j
public class PetStoreController {

	@Autowired
	private PetStoreService petStoreService;

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreData createPetStore(@RequestBody PetStoreData petStoreData) {
		log.info("Creating store {}", petStoreData);
		return petStoreService.savePetStore(petStoreData);
	}

	@PutMapping("/{petStoreId}")
	public PetStoreData updatePetStore(@PathVariable Long petStoreId, @RequestBody PetStoreData petStoreData) {
		petStoreData.setPetStoreId(petStoreId);
		log.info("Updating pet store {}", petStoreData);
		return petStoreService.savePetStore(petStoreData);
	}
	
	@GetMapping("/{petStoreId}")
	public PetStoreData returnPetStoreById(@PathVariable Long petStoreId) {
		log.info("Retrieving pet store with ID={}", petStoreId);
		return petStoreService.returnPetStoreById(petStoreId);
	}
	
	@GetMapping
	public List<PetStoreData> listAllPetStores() {
		log.info("Listing all pet stores");
		return petStoreService.retrieveAllPetStores();
	}
	
	@DeleteMapping("/{petStoreId}")
	public Map<String, String> deletePetStoreById(@PathVariable Long petStoreId) {
		log.info("Removing pet store with ID={}", petStoreId);
		petStoreService.deletePetStoreById(petStoreId);
		return Map.of("message", "Successfully deleted pet store with ID=" + petStoreId);
	}
	
	@PostMapping("/{petStoreId}/employee")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreEmployee addEmployee (@PathVariable Long petStoreId, @RequestBody PetStoreEmployee petStoreEmployee) {
		log.info("Adding employee {} to store with ID={}", petStoreEmployee, petStoreId);
		return petStoreService.saveEmployee(petStoreId, petStoreEmployee);
		
	}
	
	@PutMapping("/{petStoreId}/employee/{employeeId}")
	public PetStoreEmployee updatePetStoreEmployee (@PathVariable Long petStoreId, @PathVariable Long employeeId, @RequestBody PetStoreEmployee petStoreEmployee) {
		petStoreEmployee.setEmployeeId(employeeId);
		log.info("Updating employee {}", petStoreEmployee, petStoreId);
		return petStoreService.saveEmployee(petStoreId, petStoreEmployee);
	}
	
	@DeleteMapping("/{petStoreId}/employee/{employeeId}")
	public Map<String, String> deleteEmployeeById(@PathVariable Long petStoreId, @PathVariable Long employeeId) {
	    log.info("Removing employee with ID={} from store with ID={}", employeeId, petStoreId);
	    petStoreService.deleteEmployeeById(petStoreId, employeeId);
	    return Map.of("message", "Successfully deleted employee with ID=" + employeeId + " from store with ID=" + petStoreId);
	}

	
	@PostMapping("/{petStoreId}/customer")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreCustomer addCustomer (@PathVariable Long petStoreId, @RequestBody PetStoreCustomer petStoreCustomer) {
		log.info("Adding customer {} to store with ID={}", petStoreId, petStoreCustomer);
		return petStoreService.saveCustomer(petStoreId, petStoreCustomer);
	}
	
	@PutMapping("/{petStoreId}/customer/{customerId}")
	public PetStoreCustomer updatePetStoreCustomer (@PathVariable Long petStoreId, @PathVariable Long customerId, @RequestBody PetStoreCustomer petStoreCustomer) {
		petStoreCustomer.setCustomerId(customerId);
		log.info("Updating customer {} ", petStoreCustomer, customerId);
		return petStoreService.saveCustomer(petStoreId, petStoreCustomer);
	}
	
	@DeleteMapping("/{petStoreId}/customer/{customerId}")
	public Map<String, String> deleteCustomerById(@PathVariable Long petStoreId, @PathVariable Long customerId) {
	    log.info("Removing customer with ID={} from store with ID={}", customerId, petStoreId);
	    petStoreService.deleteCustomerById(petStoreId, customerId);
	    return Map.of("message", "Successfully deleted customer with ID=" + customerId + " from store with ID=" + petStoreId);
	}

}

