package Status

import BaseTest
import Core.NotAuthorizedException
import org.junit.Test
import org.junit.Assert.*

class TestStatusService : BaseTest() {
    @Test
    fun testGetStatuses() {
        val statusService = StatusService()


        val validUserAuthToken = "e00f1c88-1d5b-4d32-be07-1018f39a26b2"
        val validUserStatuses = statusService.getStatuses(validUserAuthToken)
        val expectedValidUserStatuses = listOf(
            Status("04e3b648-c3dd-41da-a430-1dacde995b7d", 23.2, "Work", "98729fce-0809-43fe-b953-f48b14b07616"),
            Status("12a20cb0-9e82-4d88-98f2-faaa9ff8c675", 11.0, "Shopping", "98729fce-0809-43fe-b953-f48b14b07616"),
            Status("7be3c43c-9f4b-4f6d-bbeb-e639e8331ab9", 60.0, "Home", "98729fce-0809-43fe-b953-f48b14b07616"),
            Status("d32b0786-c6f1-4c70-a31f-a9efed1ef1f6", 75.0, "Away", "98729fce-0809-43fe-b953-f48b14b07616"),
            Status("ec488303-1152-4d8d-af55-db9b323be17e", 90.5, "School", "98729fce-0809-43fe-b953-f48b14b07616")
        )
        assertEquals(
            "Should retrieve all the statuses for the group the user belongs to",
            expectedValidUserStatuses,
            validUserStatuses
        )


        val validGroupAuthToken = "7af562ed-46bd-429b-8bcd-2d85e76d9a10"
        val validGroupStatuses = statusService.getStatuses(validGroupAuthToken)
        val expectedValidGroupStatuses = listOf(
            Status(
                "32f85320-92e0-4382-a5ae-d71b562422c5",
                12.7,
                "Mortal Peril",
                "2bc8f348-fce4-4df6-9795-deff8e721c7a"
            ),
            Status("92a3db1e-99b6-45d1-92e6-2c47720e620e", 42.9, "Home", "2bc8f348-fce4-4df6-9795-deff8e721c7a"),
            Status("ed9ceea8-b059-4e00-b35e-83be6e63c497", 101.0, "School", "2bc8f348-fce4-4df6-9795-deff8e721c7a")
        )
        assertEquals(
            "Should retrieve all the statuses for the group",
            expectedValidGroupStatuses,
            validGroupStatuses
        )


        val invalidAuthToken = ""
        assertThrowsException(
            "Should have thrown 'NotAuthorizedException' because invalid AuthToken",
            NotAuthorizedException::class.java
        ) {
            val invalidStatuses = statusService.getStatuses(invalidAuthToken)
        }
    }

    @Test
    fun testUpdateStatuses() {
        val statusService = StatusService()
        val groupAuthToken = "7af562ed-46bd-429b-8bcd-2d85e76d9a10"
        val otherGroupAuthToken = "663feea8-e1e0-4cf3-89ff-ed4905ec4c7c"

        val invalidGroupIDStatuses = listOf(
            Status("92a3db1e-99b6-45d1-92e6-2c47720e620e", 44.4, "Homies", "2bc8f348-fce4-4df6-9795-deff8e721c7a"),
            Status("32f85320-92e0-4382-a5ae-d71b562422c5", 12.7, "Mortal Peril", "invalidGroupID"),
            Status(
                "95d309b7-039a-4647-8ba6-ff6dd6bb1d99",
                55.5,
                "Visiting Family",
                "2bc8f348-fce4-4df6-9795-deff8e721c7a"
            ),
            Status("970c1d21-f97b-4b35-b715-e397845d6e8f", 66.6, "Woods", "2bc8f348-fce4-4df6-9795-deff8e721c7a")
        )
        assertThrowsException(
            "Should throw Not Authorized Exception since we're trying to change a status in a different groupID.",
            NotAuthorizedException::class.java
        ) {
            statusService.updateStatuses(groupAuthToken, invalidGroupIDStatuses)
        }

        val newStatuses = listOf(
            Status("92a3db1e-99b6-45d1-92e6-2c47720e620e", 44.4, "Homies", "2bc8f348-fce4-4df6-9795-deff8e721c7a"),
            Status(
                "32f85320-92e0-4382-a5ae-d71b562422c5",
                12.7,
                "Mortal Peril",
                "2bc8f348-fce4-4df6-9795-deff8e721c7a"
            ),
            Status(
                "95d309b7-039a-4647-8ba6-ff6dd6bb1d99",
                55.5,
                "Visiting Family",
                "2bc8f348-fce4-4df6-9795-deff8e721c7a"
            ),
            Status("970c1d21-f97b-4b35-b715-e397845d6e8f", 66.6, "Woods", "2bc8f348-fce4-4df6-9795-deff8e721c7a")
        )
        statusService.updateStatuses(groupAuthToken, newStatuses)
        val updatedStatuses = statusService.getStatuses(groupAuthToken)
        assertEquals(
            "The statuses we get back for this group should be the same as we put in. No more, no less.",
            newStatuses.sortedBy { it.statusID },
            updatedStatuses
        )
        val otherStatuses = statusService.getStatuses(otherGroupAuthToken)
        val expectedOtherStatuses = listOf(
            Status("04e3b648-c3dd-41da-a430-1dacde995b7d", 23.2, "Work", "98729fce-0809-43fe-b953-f48b14b07616"),
            Status("12a20cb0-9e82-4d88-98f2-faaa9ff8c675", 11.0, "Shopping", "98729fce-0809-43fe-b953-f48b14b07616"),
            Status("7be3c43c-9f4b-4f6d-bbeb-e639e8331ab9", 60.0, "Home", "98729fce-0809-43fe-b953-f48b14b07616"),
            Status("d32b0786-c6f1-4c70-a31f-a9efed1ef1f6", 75.0, "Away", "98729fce-0809-43fe-b953-f48b14b07616"),
            Status("ec488303-1152-4d8d-af55-db9b323be17e", 90.5, "School", "98729fce-0809-43fe-b953-f48b14b07616")
        )
        assertEquals("Other statuses shouldn't be affected.", expectedOtherStatuses, otherStatuses)

        val userAuthToken = "dce470b2-0e48-4bf1-9274-0af847dab64b"
        statusService.updateStatuses(userAuthToken, listOf())
        val emptyStatuses = statusService.getStatuses(userAuthToken)
        assertEquals("All the statuses for this group should've been deleted.", listOf<Status>(), emptyStatuses)
    }
}