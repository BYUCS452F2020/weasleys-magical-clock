package com.codemonkeys.server.zone

import com.codemonkeys.server.authorization.AuthorizationTestResources
import com.codemonkeys.server.BaseDynamoTest
import com.codemonkeys.server.core.NotAuthorizedException
import com.codemonkeys.shared.zone.Zone
import org.junit.Test
import org.junit.Assert.*

class TestZoneService : BaseDynamoTest() {

    @Test
    fun testGetZones() {
        val zoneService = ServerZoneService()

        val group1Zones = zoneService.getZones(AuthorizationTestResources.GROUP_1_GROUP_AUTHTOKEN)
        assertEquals(
            "Should return zones for the group that used this authToken",
            ZoneTestResources.GROUP_1_ZONES,
            group1Zones
        )


        val group2Zones = zoneService.getZones(AuthorizationTestResources.GROUP_2_USER_AUTHTOKEN)
        assertEquals(
            "Should return zones for the group of the com.codemonkeys.server.user that used this authToken",
            ZoneTestResources.GROUP_2_ZONES,
            group2Zones
        )


        this.assertThrowsException(
            "Should throw Not Authorized Exception since a bad AuthToken was used",
            NotAuthorizedException::class.java
        )
        {
            zoneService.getZones(AuthorizationTestResources.INVALID_AUTHTOKEN)
        }
    }

    @Test
    fun testUpdateZones() {
        val zoneService = ServerZoneService()

        assertThrowsException(
            "Should throw Not Authorized Exception since we're trying to change a com.codemonkeys.server.zone in a different groupID.",
            NotAuthorizedException::class.java
        ) {
            zoneService.updateZones(
                AuthorizationTestResources.GROUP_2_GROUP_AUTHTOKEN,
                ZoneTestResources.GROUP_2_INVALID_ZONES
            )
        }

        assertThrowsException(
            "Should throw Not Authorized Exception since invalid AuthToken",
            NotAuthorizedException::class.java
        ) {
            zoneService.updateZones(
                AuthorizationTestResources.INVALID_AUTHTOKEN,
                ZoneTestResources.GROUP_2_ZONES
            )
        }

        zoneService.updateZones(AuthorizationTestResources.GROUP_2_GROUP_AUTHTOKEN, ZoneTestResources.GROUP_2_UPDATED_ZONES)
        val group2UpdatedZones = zoneService.getZones(AuthorizationTestResources.GROUP_2_USER_AUTHTOKEN)
        assertEquals("The zones for group 2 should be updated", ZoneTestResources.GROUP_2_UPDATED_ZONES.sortedBy { it.zoneID }, group2UpdatedZones.sortedBy { it.zoneID })


        val group1Zones = zoneService.getZones(AuthorizationTestResources.GROUP_1_USER_AUTHTOKEN)
        assertEquals("The zones for group 1 shouldn't have been affected", ZoneTestResources.GROUP_1_ZONES, group1Zones)


        zoneService.updateZones(AuthorizationTestResources.GROUP_2_GROUP_AUTHTOKEN, listOf<Zone>())
        val emptyZones = zoneService.getZones(AuthorizationTestResources.GROUP_2_GROUP_AUTHTOKEN)
        assertEquals("All the zones for this group should've been deleted.", listOf<Zone>(), emptyZones)
    }
}
