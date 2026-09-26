package com.payment.engine.com.payment.engine.customer.utility

import com.payment.engine.com.payment.engine.customer.model.CreateCustomerRequest
import com.payment.engine.com.payment.engine.customer.service.CustomerService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

@Component
class CustomerSimulator(
    private val customerService: CustomerService
) {

    private val log =
        LoggerFactory.getLogger(
            CustomerSimulator::class.java
        )

    private val firstNames = listOf(
        "Aarav","Aaliyah","Aaron","Abigail","Adam","Adeline","Adrian","Aisha","Akira","Alan",
        "Albert","Alessia","Alex","Alexandra","Alexander","Alice","Alicia","Alina","Amara","Amelia",
        "Amir","Amira","Ana","Anastasia","Andrea","Andrew","Angela","Anika","Anita","Anna","Anthony","Aria",
        "Ariana","Arianna","Arjun","Arthur","Aryan","Ashley","Asher","Athena","Audrey","Aurora","Ava","Ayaan",
        "Ayla","Benjamin","Bianca","Blake","Brandon","Brian","Bruno","Caleb","Camila","Camille","Carlos","Caroline","Carter","Cassandra","Catalina","Catherine","Cecilia","Celeste","Charles","Charlotte","Chloe","Christian","Christina","Christopher","Claire","Clara","Daniel","Daniela","David","Delilah","Diego","Diana","Dominic","Elena","Elias","Elijah","Eliza","Elizabeth","Ella","Ellie","Eloise","Emily","Emma","Emmanuel","Eric","Erica","Ethan","Eva","Evelyn","Ezra","Faith","Fatima","Felix","Fiona","Gabriel","Gabriella","Gael","Gemma","George","Georgia","Gianna","Giovanni","Grace","Hailey","Hannah","Harper","Hassan","Hazel","Henry","Hugo","Ibrahim","Ida","Imogen","Inaya","Isaac","Isabella","Isabelle","Isla","Ivan","Ivy","Jack","Jackson","Jacob","Jade","James","Jasmine","Javier","Jayden","Jeremiah","Jessica","Joanna","John","Jonathan","Joseph","Josephine","Joshua"
        ,"Julia","Julian","Juliana","Juliet","Kai","Kaito","Kaleb","Kamila","Karen","Katherine","Katie",
        "Kayla","Keira","Kenji","Kevin","Khadija","Kiara","Kiran","Krishna","Laila","Lara","Laura","Layla","Leah","Leo","Leonardo","Leon","Liam","Lila","Liliana","Lily","Lina","Logan","Lorenzo","Luca","Lucas","Lucia","Lucy","Luis","Luna","Lydia","Madeleine","Madeline","Madison","Maia","Maya","Mateo","Matilda","Matthew","Max","Megan","Mia","Michael","Mila","Millie","Mohammed","Nadia","Naomi","Natalia","Nathan","Nathaniel","Neha","Nicholas","Nicole","Nikhil","Nina","Noah","Nora","Noor","Olivia","Oliver","Omar","Ophelia","Oscar","Owen","Pablo","Paige","Paloma","Parker","Patrick","Paul","Penelope","Peter","Phoebe","Priya","Rafael","Raiden","Rayan","Rebecca","Reem","Reina","Remy","Ren","Rhea","Ricardo","Riley","Rina","Rishi","Robert","Robin","Rosa","Rose","Rowan","Ruby","Ryan","Sabrina","Samuel","Sara","Sarah","Sebastian","Selena","Serena","Sofia","Sophia","Sophie","Stella","Steven","Sienna","Simon","Skylar","Solomon","Stefan","Summer","Suri","Talia","Tara","Taylor","Theo","Theodore","Thomas","Tobias","Tristan","Uma","Valentina","Valerie","Vanessa","Vera","Victor","Victoria","Violet","Vivian","William","Willow","Xavier","Yasmin","Yasmine","Yuki","Zara","Zayn","Zoe","Zoey","Zoya","Zuri","Aditya","Advait","Aanya","Aarohi","Abhinav","Aditi","Akash","Alok","Ananya","Anaya","Anirudh","Anjali","Arnav","Avani","Ayush","Bhavya","Charvi","Dev","Devika","Dhruv","Diya","Esha","Ishan","Ishita","Kabir","Kavya","Manav","Meera","Mihir","Mira","Nakul","Nandini","Navya","Neel","Nisha","Pooja","Pranav","Radhika","Rahul","Raj","Rajat","Riya","Rohit","Saanvi","Sameer","Sanjay","Shreya","Siddharth","Simran","Tanvi","Varun","Ved","Vihan","Aiko","Akari","Akihiro","Aoi","Daichi","Emi","Emiko","Haru","Haruka","Hayato","Hiro","Hiroshi","Hinata","Ichiro","Kaori","Ken","Kenji","Kenta","Makoto","Mei","Michiko","Naoki","Natsuki","Riku","Rin","Sakura","Satoshi","Shiori","Takashi","Yui","Yuna","Yuto","Zahra","Zainab","Hamza","Huda","Iman","Jamal","Kareem","Khalid","Leila","Mariam","Maryam","Mustafa","Nabil","Rami","Samira","Yasir","Anders","Astrid","Bjorn","Erik","Freya","Greta","Ingrid","Lars","Linnea","Magnus","Sigrid","Sven","Anouk","Bastien","Celia","Elise","Estelle","Guillaume","Julien","Luc","Margot","Marcel","Mathilde","Pierre","Amelie","Beatrice","Benedetta","Chiara","Dante","Enzo","Francesca","Giorgio","Marco","Matteo","Silvia","Valerio","Alessandra","Alessandro","Antonella","Federico","Giulia","Massimo","Rosalia","Thiago","Valeria","Alejandro","Alondra","Andrés","Carmen","Esteban","Fernanda","Isabela","Joaquin","Marisol","Miguel","Nicolás","Santiago","Ximena","Yara","Zuleika","Amaya","Ayana","Chiamaka","Chidi","Ifeoma","Kofi","Kwame","Nia","Ngozi","Oluwaseun","Tadeo","Amina","Fatou","Idris","Kamil","Leandro","Malia",
        "Noura","Safiya","Tariq","Zahid","Elif","Esra","Leyla","Selin","Timur","Zeynep"
    )

    private val lastNames = listOf(
        "Smith","Johnson","Williams","Brown","Jones","Garcia","Miller","Davis","Rodriguez",
        "Martinez","Hernandez","Lopez","Gonzalez","Wilson","Anderson","Thomas","Taylor","Moore","Jackson","Martin","Lee","Perez","Thompson","White","Harris","Sanchez","Clark","Ramirez","Lewis","Robinson","Walker","Young","Allen","King","Wright","Scott","Torres","Nguyen","Hill","Flores","Green","Adams","Nelson","Baker","Hall","Rivera","Campbell","Mitchell","Carter","Roberts","Gomez","Phillips","Evans","Turner","Diaz","Parker","Cruz","Edwards","Collins","Reyes","Stewart","Morris","Morales","Murphy","Cook","Rogers","Gutierrez","Ortiz","Morgan","Cooper","Peterson","Bailey","Reed","Kelly","Howard","Ramos","Kim","Cox","Ward","Richardson","Watson","Brooks","Chavez","Wood","James","Bennett","Gray","Mendoza","Ruiz","Hughes","Price","Alvarez","Castillo","Sanders","Patel","Myers","Long","Ross","Foster","Jimenez","Powell","Jenkins","Perry","Russell","Sullivan","Bell","Coleman","Butler","Henderson","Barnes","Gonzales","Fisher","Vasquez","Simmons","Romero","Jordan","Patterson","Alexander","Hamilton","Graham","Reynolds","Griffin","Wallace","Moreno","West","Cole","Hayes","Bryant","Herrera","Gibson","Ellis","Tran","Medina","Aguilar","Stevens","Murray","Ford","Castro","Marshall","Owens","Harrison","Fernandez","McDonald","Woods","Washington","Kennedy","Wells","Vargas","Henry","Chen","Freeman","Webb","Tucker","Guzman","Burns","Crawford","Olson","Simpson","Porter","Hunter","Gordon","Mendez","Silva","Shaw","Snyder","Mason","Dixon","Munoz","Hunt","Hicks","Holmes","Palmer","Wagner","Black","Robertson","Boyd","Rose","Stone","Salazar","Fox","Warren","Mills","Meyer","Rice","Schmidt","Garza","Daniels","Ferguson","Nichols","Stephens","Soto","Weaver","Ryan","Gardner","Payne","Grant","Dunn","Kelley","Spencer","Hawkins","Arnold","Pierce","Vazquez","Hansen","Peters","Santos","Hart","Bradley","Knight","Elliott","Cunningham","Duncan","Armstrong","Hudson","Carroll","Lane","Riley","Andrews","Alvarado","Ray","Delgado","Berry","Perkins","Hoffman","Johnston","Matthews","Pena","Richards","Contreras","Willis","Carpenter","Lawrence","Sandoval","Gupta","Chopra","Sharma","Mehta","Shah","Verma","Singh","Kumar","Rao","Nair","Iyer","Reddy","Joshi","Kapoor","Malhotra","Bose","Das","Banerjee","Mukherjee","Chatterjee","Sengupta","Bhattacharya","Ghosh","Dutta","Roy","Sen","Mitra","Sarkar","Chakraborty","Agarwal","Bansal","Arora","Khanna","Bhatia","Desai","Patil","Kulkarni","Pillai","Menon","Nambiar","Naidu","Shetty","Pereira","Fernandes","D'Souza","Costa","Silva","Santos","Oliveira","Carvalho","Ferreira","Rodrigues","Martins","Moreira","Sousa","Ribeiro","Almeida","Correia","Lopes","Mendes","Jensen","Nielsen","Hansen","Andersen","Larsen","Johansson","Karlsson","Nilsson","Eriksson","Larsson","Petrov","Ivanov","Smirnov","Volkov","Kuznetsov","Popov","Morozov","Sokolov","Novak","Horvat","Kowalski","Nowak","Wojciechowski","Zielinski","Schneider","Fischer","Weber","Muller","Becker","Wagner","Hoffmann","Schulz","Keller","Bauer","Klein","Zimmermann","Dubois","Laurent","Moreau","Simon","Lefevre","Roux",
        "Girard","Mercier","Dupont","Fontaine","Leroy","Bernard","Martin"
    )

    private val mobileSequence =
        AtomicLong(
            Random.nextLong(
                100_000_000L,
                999_999_999L
            )
        )

    fun generateCustomers(
        count: Int
    ): SimulationResult {

        require(count in 1..10_000) {
            "Customer simulation count must be between 1 and 10,000"
        }

        val startTime =
            System.currentTimeMillis()

        log.info(
            "Customer simulation started. requestedCount={}",
            count
        )

        var created = 0
        var rejected = 0
        var failed = 0

        repeat(count) { index ->

            try {

                log.debug(
                    "Generating customer {} of {}",
                    index + 1,
                    count
                )

                val request =
                    generateCustomerRequest()

                customerService.createCustomer(
                    request
                )

                created++

            } catch (
                exception: IllegalArgumentException
            ) {

                rejected++

                log.warn(
                    "Simulated customer rejected. sequence={}, reason={}",
                    index + 1,
                    exception.message
                )

            } catch (
                exception: Exception
            ) {

                failed++

                log.error(
                    "Unexpected customer simulation failure. sequence={}",
                    index + 1,
                    exception
                )
            }
        }

        val duration =
            System.currentTimeMillis() - startTime

        log.info(
            "Customer simulation completed. requested={}, created={}, rejected={}, failed={}, durationMs={}",
            count,
            created,
            rejected,
            failed,
            duration
        )

        return SimulationResult(
            requested = count,
            created = created,
            rejected = rejected,
            failed = failed,
            durationMs = duration
        )
    }

    private fun generateCustomerRequest():
            CreateCustomerRequest {

        val firstName =
            firstNames.random()

        val lastName =
            lastNames.random()

        val mobile =
            generateMobileNumber()

        val email =
            "${firstName.lowercase()}.${lastName.lowercase()}.${mobile.takeLast(4)}@simulation.local"

        log.debug(
            "Synthetic customer generated. firstName={}, mobile={}",
            firstName,
            mobile
        )

        return CreateCustomerRequest(
            firstName = firstName,
            lastName = lastName,
            mobileNumber = mobile,
            email = email
        )
    }

    private fun generateMobileNumber(): String {

        val sequence =
            mobileSequence.incrementAndGet()

        val number =
            sequence
                .toString()
                .padStart(9, '0')

        val firstDigit =
            when (sequence % 4) {
                0L -> "6"
                1L -> "7"
                2L -> "8"
                else -> "9"
            }

        return "+91$firstDigit${number.takeLast(9)}"
    }
}