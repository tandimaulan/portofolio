//package com.voltras.blockseatservice.utils;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.validation.Valid;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//
//import com.voltras.blockseat.api.models.Airport;
//import com.voltras.blockseat.api.models.FlightSegment;
//import com.voltras.blockseat.api.models.FlightSummary;
//import com.voltras.blockseat.api.models.JourneyOption;
//import com.voltras.blockseat.api.models.LocationInfo;
//
//public class DummyUtils {
//
//    public List<JourneyOption> dummyResponseCreator() {
//        List<JourneyOption> options = new ArrayList<>();
//
//        // Dummy SegmentSummary FlightSummary Section
//
//        @Valid
//        LocationInfo xdeparture = new LocationInfo(
//                "CGK",
//                "Soekarno-Hatta International Airport",
//                "Jakarta",
//                "Indonesia",
//                LocalDateTime.of(2023, 6, 1, 21, 45, 00)); // "2023-06-01T21:45:00"
//
//        @Valid
//        LocationInfo xarrival = new LocationInfo(
//                "JED",
//                "King Abdulaziz International Airport",
//                "Jeddah",
//                "Saudi Arabia",
//                LocalDateTime.of(2023, 6, 3, 02, 10, 00)); // "2023-06-03T02:10:00"
//
//        @NotEmpty
//        List<String> listOperatingAirline = new ArrayList<>();
//        listOperatingAirline.add("EK");
//
//        List<Airport> transitPoints = new ArrayList<>();
//        Airport airport01 = new Airport(
//                "SIN",
//                "Singapore Changi Airport",
//                "Singapore",
//                "Singapore");
//        Airport airport02 = new Airport(
//                "DXB",
//                "Dubai International Airport",
//                "Dubai",
//                "United Arab Emirates");
//        transitPoints.add(airport01);
//        transitPoints.add(airport02);
//
//        @NotNull
//        @Valid
//        FlightSummary summaryDummy01 = new FlightSummary(
//                xdeparture,
//                xarrival,
//                1455,
//                listOperatingAirline,
//                2,
//                transitPoints);
//
//        /// Dummy SegmentDetails Section
//
//        @NotEmpty
//        List<FlightSegment> segDetailDummy01 = new ArrayList<>();
//
//        @Valid
//        LocationInfo depSeg01 = new LocationInfo(
//                "CGK",
//                "Soekarno-Hatta International Airport",
//                "Jakarta",
//                "Indonesia",
//                LocalDateTime.of(2023, 6, 1, 21, 45, 00)); // "2023-06-01T21:45:00"
//        @Valid
//        LocationInfo arrSeg01 = new LocationInfo(
//                "SIN",
//                "Singapore Changi Airport",
//                "Singapore",
//                "Singapore",
//                LocalDateTime.of(2023, 6, 2, 00, 40, 00)); // "2023-06-02T00:40:00"
//        FlightSegment fltDetDum01 = new FlightSegment(
//                depSeg01,
//                arrSeg01,
//                115,
//                "EK",
//                "EK7765",
//                0,
//                "B-747",
//                0);
//
//        segDetailDummy01.add(fltDetDum01);
//
//        @Valid
//        LocationInfo depSeg02 = new LocationInfo(
//                "SIN",
//                "Singapore Changi Airport",
//                "Singapore",
//                "Singapore",
//                LocalDateTime.of(2023, 6, 2, 9, 40, 00)); // "2023-06-02T09:40:00"
//        @Valid
//        LocationInfo arrSeg02 = new LocationInfo(
//                "DXB",
//                "Dubai International Airport",
//                "Dubai",
//                "United Arab Emirates",
//                LocalDateTime.of(2023, 6, 2, 13, 00, 00)); // "2023-06-02T13:00:00"
//        FlightSegment fltDetDum02 = new FlightSegment(
//                depSeg02,
//                arrSeg02,
//                440,
//                "EK",
//                "EK405",
//                0,
//                "B-747",
//                540);
//
//        segDetailDummy01.add(fltDetDum02);
//
//        @Valid
//        LocationInfo depSeg03 = new LocationInfo(
//                "DXB",
//                "Dubai International Airport",
//                "Dubai",
//                "United Arab Emirates",
//                LocalDateTime.of(2023, 6, 3, 00, 20, 00)); // "2023-06-03T00:20:00"
//        @Valid
//        LocationInfo arrSeg03 = new LocationInfo(
//                "JED",
//                "King Abdulaziz International Airport",
//                "Jeddah",
//                "Saudi Arabia",
//                LocalDateTime.of(2023, 6, 3, 02, 10, 00)); // "2023-06-03T02:10:00"
//        FlightSegment fltDetDum03 = new FlightSegment(
//                depSeg03,
//                arrSeg03,
//                170,
//                "EK",
//                "EK801",
//                0,
//                "B-747",
//                680);
//        segDetailDummy01.add(fltDetDum03);
//
//        @NotNull
//        List<String> dummyNotes = new ArrayList<>();
//        JourneyOption dummy01 = new JourneyOption(
//                "123456",
//                75,
//                75000000.00,
//                25000000.00,
//                2,
//                summaryDummy01,
//                segDetailDummy01,
//                dummyNotes);
//
//        options.add(dummy01);
//
//        @Valid
//        LocationInfo xdeparture2 = new LocationInfo(
//                "MED",
//                "Prince Mohammad Bin Abdulaziz Airport",
//                "Madinah",
//                "Saudi Arabia",
//                LocalDateTime.of(2023, 6, 15, 18, 15, 00)); // "2023-06-15T18:15:00"
//        @Valid
//        LocationInfo xarrival2 = new LocationInfo(
//                "CGK",
//                "Soekarno-Hatta International Airport",
//                "Jakarta",
//                "Indonesia",
//                LocalDateTime.of(2023, 6, 15, 17, 20, 00)); // "2023-06-16T17:20:00"
//        @NotEmpty
//        List<String> listOperatingAirline2 = new ArrayList<>();
//        listOperatingAirline.add("SV");
//        List<Airport> transitPoints2 = new ArrayList<>();
//        Airport airport03 = new Airport(
//                "CAI",
//                "Cairo International Airport",
//                "Cairo",
//                "Egypt");
//        Airport airport04 = new Airport(
//                "JED",
//                "King Abdulaziz International Airport",
//                "Jeddah",
//                "Saudi Arabia");
//        transitPoints.add(airport03);
//        transitPoints.add(airport04);
//
//        @NotNull
//        @Valid
//        FlightSummary summaryDummy02 = new FlightSummary(
//                xdeparture2,
//                xarrival2,
//                1455,
//                listOperatingAirline2,
//                2,
//                transitPoints2);
//
//        @NotEmpty
//        List<FlightSegment> segDetailDummy02 = new ArrayList<>();
//
//        @Valid
//        LocationInfo depSeg07 = new LocationInfo(
//                "MED",
//                "Prince Mohammad Bin Abdulaziz Airport",
//                "Madinah",
//                "Saudi Arabia", LocalDateTime.of(2023, 6, 15, 18, 15, 00)); // "2023-06-15T18:15:00"
//        @Valid
//        LocationInfo arrSeg07 = new LocationInfo(
//                "CAI",
//                "Cairo International Airport",
//                "Cairo",
//                "Egypt",
//                LocalDateTime.of(2023, 6, 15, 20, 10, 00)); // "2023-06-15T20:10:00"
//        FlightSegment fltDetDum04 = new FlightSegment(
//                depSeg07,
//                arrSeg07,
//                175,
//                "SV",
//                "SV381",
//                0,
//                "A320",
//                0);
//        segDetailDummy02.add(fltDetDum04);
//
//        @Valid
//        LocationInfo depSeg08 = new LocationInfo(
//                "CAI",
//                "Cairo International Airport",
//                "Cairo",
//                "Egypt",
//                LocalDateTime.of(2023, 6, 15, 22, 50, 00)); // "2023-06-15T22:50:00"
//        @Valid
//        LocationInfo arrSeg08 = new LocationInfo(
//                "JED",
//                "King Abdulaziz International Airport",
//                "Jeddah",
//                "Saudi Arabia",
//                LocalDateTime.of(2023, 6, 16, 01, 10, 00)); // "2023-06-16T01:10:00"
//        FlightSegment fltDetDum05 = new FlightSegment(
//                depSeg08,
//                arrSeg08,
//                80,
//                "SV",
//                "SV388",
//                0,
//                "A320",
//                160);
//        segDetailDummy02.add(fltDetDum05);
//
//        @Valid
//        LocationInfo depSeg09 = new LocationInfo(
//                "JED",
//                "King Abdulaziz International Airport",
//                "Jeddah",
//                "Saudi Arabia",
//                LocalDateTime.of(2023, 6, 16, 03, 10, 00)); // "2023-06-16T03:10:00"
//        @Valid
//        LocationInfo arrSeg09 = new LocationInfo(
//                "CGK",
//                "Soekarno-Hatta International Airport",
//                "Jakarta",
//                "Indonesia",
//                LocalDateTime.of(2023, 6, 16, 17, 20, 00)); // "2023-06-16T17:20:00"
//        FlightSegment fltDetDum06 = new FlightSegment(
//                depSeg09,
//                arrSeg09,
//                610,
//                "SV",
//                "SV818",
//                0,
//                "A320",
//                120);
//        segDetailDummy02.add(fltDetDum06);
//
//        JourneyOption dummy02 = new JourneyOption(
//                "1234567",
//                80,
//                75000000.00,
//                25000000.00,
//                2,
//                summaryDummy02,
//                segDetailDummy02,
//                dummyNotes);
//
//        options.add(dummy02);
//
//        return options;
//
//    }
//
//    public List<JourneyOption> dummyResponse02() {
//        List<JourneyOption> options = new ArrayList<>();
//        List<String> dummyNotes = new ArrayList<>();
//
//        @Valid
//        LocationInfo cgk_tlv_dep = new LocationInfo(
//                "CGK",
//                "Soekarno-Hatta International Airport",
//                "Jakarta",
//                "Indonesia",
//                LocalDateTime.of(2023, 7, 1, 0, 40, 00)); // "2023-07-01T00:40:00"
//        @Valid
//        LocationInfo cgk_tlv_arr = new LocationInfo(
//                "TLV",
//                "Ben Gurion International Airport",
//                "Tel-aviv",
//                "Israel",
//                LocalDateTime.of(2023, 7, 1, 17, 35, 00)); // "2023-07-01T17:35:00"
//        List<Airport> cgk_tlv_transits = new ArrayList<>();
//        Airport cgk_tlv_transit = new Airport(
//                "DXB",
//                "Dubai International Airport",
//                "Dubai",
//                "United Arab Emirates");
//        cgk_tlv_transits.add(cgk_tlv_transit);
//        @NotEmpty
//        List<String> listOperatingAirline = new ArrayList<>();
//        listOperatingAirline.add("EK");
//        @NotNull
//        @Valid
//        FlightSummary cgk_tlv_summ = new FlightSummary(
//                cgk_tlv_dep,
//                cgk_tlv_arr,
//                1315,
//                listOperatingAirline,
//                1,
//                cgk_tlv_transits);
//        @NotEmpty
//        List<FlightSegment> cgk_tlv_details = new ArrayList<>();
//        @Valid
//        LocationInfo cgk_dxb_dep = new LocationInfo(
//                "CGK",
//                "Soekarno-Hatta International Airport",
//                "Jakarta",
//                "Indonesia",
//                LocalDateTime.of(2023, 7, 1, 0, 40, 00)); // "2023-07-01T00:40:00"
//        @Valid
//        LocationInfo cgk_dxb_arr = new LocationInfo(
//                "DXB",
//                "Dubai International Airport",
//                "Dubai",
//                "United Arab Emirates",
//                LocalDateTime.of(2023, 7, 1, 5, 30, 00)); // "2023-07-01T05:30:00"
//        FlightSegment cgk_dxb = new FlightSegment(
//                cgk_dxb_dep,
//                cgk_dxb_arr,
//                470,
//                "EK",
//                "EK359",
//                0,
//                "A320",
//                0);
//
//        cgk_tlv_details.add(cgk_dxb);
//
//        @Valid
//        LocationInfo dxb_tlv_dep = new LocationInfo(
//                "DXB",
//                "Dubai International Airport",
//                "Dubai",
//                "United Arab Emirates",
//                LocalDateTime.of(2023, 7, 1, 15, 20, 00)); // "2023-07-01T15:20:00"
//        @Valid
//        LocationInfo dxb_tlv_arr = new LocationInfo(
//                "TLV",
//                "Ben Gurion International Airport",
//                "Tel-aviv",
//                "Israel",
//                LocalDateTime.of(2023, 7, 1, 17, 35, 00)); // "2023-07-01T17:35:00"
//        FlightSegment dxb_tlv = new FlightSegment(
//                dxb_tlv_dep,
//                dxb_tlv_arr,
//                255,
//                "EK",
//                "EK931",
//                0,
//                "A320",
//                590);
//        cgk_tlv_details.add(dxb_tlv);
//
//        JourneyOption cgk_tlv = new JourneyOption(
//                "19ae6a74-7001-4588-9a2d-d1d67c6a0cf2",
//                50,
//                10000000.0,
//                3000000.0,
//                2,
//                cgk_tlv_summ,
//                cgk_tlv_details,
//                dummyNotes);
//
//        options.add(cgk_tlv);
//
//        @Valid
//        LocationInfo tlv_fco_dep = new LocationInfo(
//                "TLV",
//                "Ben Gurion International Airport",
//                "Tel-aviv",
//                "Israel",
//                LocalDateTime.of(2023, 7, 6, 06, 20, 00)); // "2023-07-06T06:20:00"
//        @Valid
//        LocationInfo tlv_fco_arr = new LocationInfo(
//                "FCO",
//                "Leonardo da Vinci–Fiumicino Airport",
//                "Rome",
//                "Italy",
//                LocalDateTime.of(2023, 7, 6, 11, 10, 00)); // "2023-07-06T11:10:00"
//        List<Airport> tlv_fco_transits = new ArrayList<>();
//        Airport tlv_fco_transit = new Airport(
//                "SAW",
//                "Sabiha Gökçen International Airport",
//                "Istanbul",
//                "Turkey");
//        tlv_fco_transits.add(tlv_fco_transit);
//        @NotEmpty
//        List<String> listOperatingAirline2 = new ArrayList<>();
//        listOperatingAirline.add("TK");
//
//        @NotNull
//        @Valid
//        FlightSummary tlv_fco_summ = new FlightSummary(
//                tlv_fco_dep,
//                tlv_fco_arr,
//                350,
//                listOperatingAirline2,
//                1,
//                tlv_fco_transits);
//
//        @NotEmpty
//        List<FlightSegment> tlv_fco_details = new ArrayList<>();
//        @Valid
//        LocationInfo tlv_saw_dep = new LocationInfo(
//                "TLV",
//                "Ben Gurion International Airport",
//                "Tel-aviv",
//                "Israel",
//                LocalDateTime.of(2023, 7, 6, 06, 20, 00)); // "2023-07-06T06:20:00"
//        @Valid
//        LocationInfo tlv_saw_arr = new LocationInfo(
//                "SAW",
//                "Sabiha Gökçen International Airport",
//                "Istanbul",
//                "Turkey",
//                LocalDateTime.of(2023, 7, 6, 8, 20, 00)); // "2023-07-06T08:20:00"
//        FlightSegment tlv_saw = new FlightSegment(
//                tlv_saw_dep,
//                tlv_saw_arr,
//                120,
//                "TK",
//                "TK7711",
//                0,
//                "",
//                0);
//        tlv_fco_details.add(tlv_saw);
//
//        @Valid
//        LocationInfo saw_fco_dep = new LocationInfo(
//                "SAW",
//                "Sabiha Gökçen International Airport",
//                "Istanbul",
//                "Turkey",
//                LocalDateTime.of(2023, 7, 6, 9, 35, 00)); // "2023-07-06T09:35:00"
//        @Valid
//        LocationInfo saw_fco_arr = new LocationInfo(
//                "FCO",
//                "Leonardo da Vinci–Fiumicino Airport",
//                "Rome",
//                "Italy",
//                LocalDateTime.of(2023, 7, 6, 11, 10, 00)); // "2023-07-06T11:10:00"
//        FlightSegment saw_fco = new FlightSegment(
//                saw_fco_dep,
//                saw_fco_arr,
//                155,
//                "TK",
//                "TK7660",
//                0,
//                "",
//                75);
//        tlv_fco_details.add(saw_fco);
//
//        JourneyOption tlv_fco = new JourneyOption(
//                "f5ad4234-1b7c-4988-88c2-9725e238b65a",
//                50,
//                10000000.0,
//                3000000.0,
//                2,
//                tlv_fco_summ,
//                tlv_fco_details,
//                dummyNotes);
//        options.add(tlv_fco);
//
//        @Valid
//        LocationInfo fco_cgk_dep = new LocationInfo(
//                "FCO",
//                "Leonardo da Vinci–Fiumicino Airport",
//                "Rome",
//                "Italy",
//                LocalDateTime.of(2023, 7, 15, 10, 20, 00)); // "2023-07-15T10:20:00"
//        @Valid
//        LocationInfo fco_cgk_arr = new LocationInfo(
//                "CGK",
//                "Soekarno-Hatta International Airport",
//                "Jakarta",
//                "Indonesia",
//                LocalDateTime.of(2023, 7, 16, 14, 35, 00)); // "2023-07-16T14:35:00"
//        List<Airport> fco_cgk_transits = new ArrayList<>();
//        Airport fco_cgk_transit = new Airport(
//                "AUH",
//                "Abu Dhabi International Airport",
//                "Abu Dhabi",
//                "United Arab Emirates");
//        fco_cgk_transits.add(fco_cgk_transit);
//        @NotEmpty
//        List<String> listOperatingAirline3 = new ArrayList<>();
//        listOperatingAirline.add("EY");
//        @NotNull
//        @Valid
//        FlightSummary fco_cgk_summ = new FlightSummary(
//                fco_cgk_dep,
//                fco_cgk_arr,
//                1335,
//                listOperatingAirline3,
//                1,
//                fco_cgk_transits);
//        @NotEmpty
//        List<FlightSegment> fco_cgk_details = new ArrayList<>();
//        @Valid
//        LocationInfo fco_auh_dep = new LocationInfo(
//                "FCO",
//                "Leonardo da Vinci–Fiumicino Airport",
//                "Rome",
//                "Italy",
//                LocalDateTime.of(2023, 7, 15, 10, 20, 00)); // "2023-07-15T10:20:00"
//        @Valid
//        LocationInfo fco_auh_arr = new LocationInfo(
//                "AUH",
//                "Abu Dhabi International Airport",
//                "Abu Dhabi",
//                "United Arab Emirates",
//                LocalDateTime.of(2023, 7, 15, 18, 25, 00)); // "2023-07-15T18:25:00"
//        FlightSegment fco_auh = new FlightSegment(
//                fco_auh_dep,
//                fco_auh_arr,
//                305,
//                "EY",
//                "EY86",
//                0,
//                "",
//                0);
//        fco_cgk_details.add(fco_auh);
//        @Valid
//        LocationInfo auh_cgk_dep = new LocationInfo(
//                "AUH",
//                "Abu Dhabi International Airport",
//                "Abu Dhabi",
//                "United Arab Emirates",
//                LocalDateTime.of(2023, 7, 16, 3, 05, 00)); // "2023-07-16T03:05:00"
//        @Valid
//        LocationInfo auh_cgk_arr = new LocationInfo(
//                "CGK",
//                "Soekarno-Hatta International Airport",
//                "Jakarta",
//                "Indonesia",
//                LocalDateTime.of(2023, 7, 16, 14, 35, 00)); // "2023-07-16T14:35:00"
//        FlightSegment auh_cgk = new FlightSegment(
//                auh_cgk_dep,
//                auh_cgk_arr,
//                510,
//                "EY",
//                "EY474",
//                0,
//                "",
//                520);
//        fco_cgk_details.add(auh_cgk);
//        JourneyOption fco_cgk = new JourneyOption(
//                "85777916-1b5e-4b7a-9a66-a1c8494454ea",
//                50,
//                10000000.0,
//                3000000.0,
//                2,
//                fco_cgk_summ,
//                fco_cgk_details,
//                dummyNotes);
//        options.add(fco_cgk);
//
//        return options;
//    }
//}
