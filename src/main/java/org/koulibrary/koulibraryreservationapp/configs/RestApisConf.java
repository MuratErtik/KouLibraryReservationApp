package org.koulibrary.koulibraryreservationapp.configs;

public class RestApisConf {

    public static final String DEVELOPER = "/dev";
    public static final String RELEASE = "/prod";
    public static final String VERSIONS = "/v1";



    public static final String LIBRARYCONTROLLER = DEVELOPER+VERSIONS+"/libraries";

    public static final String LIBRARYCLOSURECONTROLLER = "/{libraryId}/library-closures";

    public static final String LIBRARYWORKINGHOURSCONTROLLER = "/{libraryId}/library-working-hours";

    public static final String SALOONCONTROLLER = LIBRARYCONTROLLER+"/{libraryId}"+"/saloons";

    public static final String SALOONCLOSURECONTROLLER = "/{saloonId}"+"/saloon-closures";

    public static final String SALOONWORKINGHOURSCONTROLLER = "/{saloonId}"+"/saloon-working-hours";

    public static final String DESKCONTROLLER = SALOONCONTROLLER+"/{saloonId}"+"/desks";

    public static final String LIBRARYTIMESLOTCONTROLLER = LIBRARYCONTROLLER+"/{libraryId}/timeslots}";




}
