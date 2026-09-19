package com.example.dvotesystem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.List;

public class ConstituencySeeder {

    public static void seedData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("States");

        List<String> apConstituencies = Arrays.asList(
            "Achanta", "Addanki", "Adoni", "Allagadda", "Alur", "Amadalavalasa", "Amalapuram", "Anakapalle", "Anantapur Urban", "Anaparthy", 
            "Araku Valley", "Atmakur", "Avanigadda", "Badvel", "Banaganapalle", "Bapatla", "Bhimavaram", "Bobbili", "Chandragiri", "Cheepurupalli", 
            "Chilakaluripet", "Chintalapudi", "Chirala", "Chittoor", "Darsi", "Dharmavaram", "Elamanchili", "Eluru", "Etcherla", "Gajapathinagaram", 
            "Gajuwaka", "Gangadhara Nellore", "Gannavaram", "Gannavaram (Krishna)", "Giddalur", "Gopalapuram", "Gudivada", "Gudur", "Guntakal", "Guntur East", 
            "Guntur West", "Gurazala", "Hindupur", "Ichchapuram", "Jaggampeta", "Jammalamadugu", "Kadapa", "Kadiri", "Kaikalur", "Kakinada City", 
            "Kakinada Rural", "Kalyandurg", "Kamalapuram", "Kanigiri", "Kavali", "Kodumur", "Kodur", "Kondapi", "Kothapeta", "Kovur", 
            "Kovvur", "Kuppam", "Kurnool", "Kurupam", "Macherla", "Madakasira", "Madanapalle", "Madugula", "Mandapeta", "Mangalagiri", 
            "Mantralayam", "Markapuram", "Mummidivaram", "Mydukur", "Nagari", "Nandigama", "Nandikotkur", "Nandyal", "Narasannapeta", "Narasapuram", 
            "Narasaraopet", "Narsipatnam", "Nellimarla", "Nellore City", "Nellore Rural", "Nuzvid", "Ongole", "Paderu", "Palacole", "Palakonda", 
            "Palamaner", "Pamarru", "Panyam", "Parchur", "Parvathipuram", "Pathapatnam", "Payakaraopet", "Pedakurapadu", "Pedana", "Peddapuram", 
            "Penamaluru", "Pendurthi", "Penukonda", "Pileru", "Pithapuram", "Polavaram", "Prathipadu (Guntur)", "Prathipadu (Kakinada)", "Pulivendula", "Punganur", 
            "Puttur", "Rajahmundry City", "Rajahmundry Rural", "Rajam", "Rajampet", "Rajanagaram", "Ramachandrapuram", "Rampachodavaram", "Rapthadu", "Rayachoti", 
            "Rayadurg", "Razole", "Repalle", "Salur", "Santhanuthalapadu", "Sattenapalle", "Satyavedu", "Singanamala", "Sompeta", "Srikakulam", 
            "Srisailam", "Sullurpeta", "Tadepalligudem", "Tadikonda", "Tadpatri", "Tanuku", "Tekkali", "Tenali", "Thamballapalle", "Tirupati", 
            "Tiruvuru", "Tuni", "Udayagiri", "Undi", "Unguturu", "Uravakonda", "Venkatagiri", "Vijayawada Central", "Vijayawada East", "Vijayawada West", 
            "Vinukonda", "Visakhapatnam East", "Visakhapatnam North", "Visakhapatnam South", "Visakhapatnam West", "Vizianagaram", "Yelamanchili", "Yemmiganur", "Yerragondapalem"
        );

        List<String> tsConstituencies = Arrays.asList(
            "Achampet", "Adilabad", "Alair", "Alampur", "Amberpet", "Andole", "Armur", "Asifabad", "Aswaraopeta", "Bahadurpura", 
            "Balkonda", "Banswada", "Bellampalli", "Bhadradri Kothagudem", "Bhadrachalam", "Bhongir", "Bhupalpalle", "Boath", "Bodhan", "Chandrayangutta", 
            "Charminar", "Chennur", "Chevella", "Choppadandi", "Devarakonda", "Devarkadra", "Dharmapuri", "Dornakal", "Dubbak", "Gadwal", 
            "Gajwel", "Ghanpur (Station)", "Goshamahal", "Husnabad", "Huzurabad", "Huzurnagar", "Ibrahimpatnam", "Jadcherla", "Jagtial", "Jangaon", 
            "Jubilee Hills", "Kalwakurthy", "Kamareddy", "Karimnagar", "Karwan", "Khairatabad", "Khammam", "Khanapur", "Kodad", "Kodangal", 
            "Kollapur", "Koratla", "Kothagudem", "Kukatpally", "Lal Bahadur Nagar", "Madhira", "Mahabubabad", "Mahabubnagar", "Maheshwaram", "Makthal", 
            "Manakondur", "Mancherial", "Manthani", "Medak", "Medchal", "Miryalaguda", "Mudhole", "Musheerabad", "Nagarkurnool", "Nagarjuna Sagar", 
            "Nakrekal", "Nalgonda", "Nampally", "Narayankhed", "Narayanpet", "Narsampet", "Narsapur", "Nirmal", "Nizamabad (Rural)", "Nizamabad Urban", 
            "Palair", "Palakurthi", "Pargi", "Parkal", "Patancheru", "Peddapalle", "Pinapaka", "Quthbullapur", "Rajendranagar", "Ramagundam", 
            "Sanathnagar", "Sangareddy", "Sathupalle", "Secunderabad", "Secunderabad Cantonment", "Serilingampally", "Shadnagar", "Siddipet", "Sircilla", "Sirpur", 
            "Suryapet", "Tandur", "Thungathurthi", "Uppal", "Vaira", "Vemulawada", "Wanaparthy", "Waradhanapet", "Warangal East", "Warangal West", 
            "Wyra", "Yakutpura", "Yellandu", "Yellareddy", "Zaheerabad"
        );

        ref.child("Andhra Pradesh").child("Constituencies").setValue(apConstituencies);
        ref.child("Telangana").child("Constituencies").setValue(tsConstituencies);
    }
}
