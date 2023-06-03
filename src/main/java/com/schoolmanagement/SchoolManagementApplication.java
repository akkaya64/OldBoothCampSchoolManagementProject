package com.schoolmanagement;

import com.schoolmanagement.entity.enums.Gender;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.payload.request.AdminRequest;
import com.schoolmanagement.service.AdminService;
import com.schoolmanagement.service.UserRoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class SchoolManagementApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;

	private final AdminService adminService;

	public SchoolManagementApplication(UserRoleService userRoleService, AdminService adminService) {
		this.userRoleService = userRoleService;
		this.adminService = adminService;
	}

	// Bu uygulama ilk basladiginda uygulamayi ayaga kaldirmak icin rolleri ve Admini setleyebelmek icin bu
	// Runner Class i CommandLineRunner dan implement ediyoruz. bunlari yazdigimizda CTE hatasi verecek imleci hatanin
	// uzerine geldigimizde bizden implement methods yapmamizi isteyecek buna tikladigimizda acilan pencerecden methodu
	// secip OK tusuna basacagiz

	public static void main(String[] args) {
		SpringApplication.run(SchoolManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// CommandLineRunner interface implement edilince @Override edilen run methodu otamatik olarak calisiyor
		// Uygulama ile ilgili endPointler calismadan once bu method calisiyor Burada DB ile ilgili islemleri yapacagiz
		// Role bilgilerini, Admin ile ilgili islemleri tanimlayacagiz. Boylelikle Uygulama ayaga kalktigi anda DB de
		// Roller ile ilgili ve bir tane Built in Admin ilgili tablolari olusmus olacak.
		// Uygulama baslatildiginda otomatik olarak calismasini istedigim butun code lari bu run methodunun
		// icine yaziyoruz.

		// Role Tablosunu dolduracagiz
		// Built in olan bir Admin olusturulacak

		// Role icin: Role tablosunu doldurabilmek icin bir bagimlilik eklmek gerekiyor creation islemlerini en
		// mantiklisi userRoleService uzerinden yapmak yukariya bu classi enjecte ediyoruz Role tablosuna gidecegiz
		// sayi 0 ise userRoleService Classinda creation islemlerini yapacak bir methoda ihtiyacimiz olacak

		if(userRoleService.getAllUserRole().size()==0) { // getAllUserRole() methodu henuz yok birazdan olusturulacak
			// userRoleRepository.count() == 0 count() methodu olusturup bunuda kullanabilirdik
			// Eger DB deki tablo bossa save methodunu calistiracak eger doluysa integer bir deger dondurecek. Bunu size
			// dedigimize gore List yapisinda donecek (Count da diyebilirdik) Role Tablosundaki rolleri list yapisinda
			// cekecek hic role yoksa Listin size i 0 olacak. eger role tablosu bossa git asagidaki rolleri olustur diyecegiz.

			userRoleService.save(RoleType.ADMIN);//save() methodu da henuz yok birazdan olusturacagiz
			userRoleService.save(RoleType.MANAGER);//save() methodu da henuz yok birazdan olusturacagiz
			userRoleService.save(RoleType.ASSISTANTMANAGER);//save() methodu da henuz yok birazdan olusturacagiz
			userRoleService.save(RoleType.TEACHER);//save() methodu da henuz yok birazdan olusturacagiz
			userRoleService.save(RoleType.STUDENT);//save() methodu da henuz yok birazdan olusturacagiz
			userRoleService.save(RoleType.ADVISORTEACHER);//save() methodu da henuz yok birazdan olusturacagiz
			userRoleService.save(RoleType.GUESTUSER);//save() methodu da henuz yok birazdan olusturacagiz

			// ODEV:

		}

		//Built in  bir Admin olusturabilmek icin AdminService Classini yukarida enjekte ediyoruz.
		//Bir Amin olup olmadigini kontrol etmemiz lazim sayi AdminRole sayisi 0 ise userRoleService Classinda
		//creation islemlerini yapacak bir methoda ihtiyacimiz olacak. bir Admin varsa zaten o Built in dir.

		if(adminService.countAllAdmin()==0) { // AdminRole sayisi si 0 sa Bir Admin olusturacagiz. Birazdan
			// countAllAdmin() methodu olusturacagiz
			AdminRequest admin = new AdminRequest();
			admin.setUsername("Admin");// admin tablosuna Admin rolunu ekliyoruz. Admin save isleminde eger Username i
			// Admin ise  bunu otomatikmen Built in yap demistik. admin.setUsername("Admin");'e parametre olarak Admin
			// verirsek bu setlenen admin otamatikmen Built in olmus oluyor.
			admin.setSsn("987-99-9999");
			admin.setPassword("12345678");
			admin.setName("Admin");
			admin.setSurname("Admin");
			admin.setPhoneNumber("555-444-4321");
			admin.setGender(Gender.FEMALE);
			admin.setBirthDay(LocalDate.of(2002,2,2));
			admin.setBirthPlace("US");
			adminService.save(admin);

			// POJO class olusturmadan AdminRequest uzerinden turetmemizin sebebi Service katmaninda hazir methodumuz var
			// ama bu DTO aliyor. az once yazdigimiz save methodu DTO aliyor baska bir sebebi yok. AdminRequest() den
			// AdminService katmanina ulasabiliyoruz.

		}

	}
}
