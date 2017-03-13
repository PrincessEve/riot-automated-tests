package mobilestests_android;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import pom_android.RiotContactPickerPageObjects;
import pom_android.RiotNewChatPageObjects;
import pom_android.RiotRoomDetailsPageObjects;
import pom_android.RiotRoomPageObjects;
import pom_android.RiotRoomsListPageObjects;
import utility.Constant;
import utility.RiotParentTest;
import utility.ScreenshotUtility;

/**
 * TODO CHECK THAT ACCESS CONTACT PERMISSION IS TICKED IN SETTINGS
 * Tests on Direct Message.
 * @author jeang
 *
 */
@Listeners({ ScreenshotUtility.class })
public class RiotDirectMessagesTests extends RiotParentTest{
private String riotuser1DisplayName="riotuser6";
private String riotuser2DisplayName="riotuser7";
private String riotuser2MatrixId="@riotuser7:matrix.org";
private String riotuser3DisplayName="riotuser8";
private String riotuser3MatrixId="@riotuser8:matrix.org";
private String tmpRoomName="tmp room DM";

	/**
	 * Start a chat </br>
	 * Invite just on other user </br>
	 * Check that the new room doesn't have a little green man on both devices.</br>
	 * Try to start a newt chat with this same member : check that the previous room is opened instead.
	 * @throws InterruptedException
	 */
	@Test(groups={"2drivers_android","checkuser"}, description="direct message test")
	public void startChatWithOneUserTwice() throws InterruptedException{
		int randInt = 1 + (int)(Math.random() * ((10000 - 1) + 1));
		String randomMsg=(new StringBuilder("direct chat test").append(randInt)).toString();

		/*
		 * FIRST PART
		 */
		RiotRoomsListPageObjects roomsListDevice1=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver1());
		RiotRoomsListPageObjects roomsListDevice2=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver2());
		//create a new "start chat" room
		roomsListDevice1.plusRoomButton.click();
		roomsListDevice1.startChatCheckedTextView.click();
		//invite a member
		RiotContactPickerPageObjects inviteViewDevice1=new RiotContactPickerPageObjects(appiumFactory.getAndroidDriver1());
		inviteViewDevice1.searchAndSelectMember(riotuser2MatrixId);
		RiotNewChatPageObjects newChatViewDevice1= new RiotNewChatPageObjects(appiumFactory.getAndroidDriver1());
		newChatViewDevice1.confirmRoomCreationButton.click();
		//Room is created, then go back in rooms list
		RiotRoomPageObjects newRoomDevice1= new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		newRoomDevice1.menuBackButton.click();
		//assertion on the inviter device: the new room is seen as an direct message room.
		Assert.assertTrue(roomsListDevice1.isDirectMessageByRoomName(riotuser2DisplayName),"Room "+riotuser2DisplayName+" doesn't have a little green man on inviter device.");
		//assertion the invitee device (device 2)
		//TODO: check little green man on invitation layout
		roomsListDevice2.previewInvitation(riotuser1DisplayName);
		RiotRoomPageObjects newRoomDevice2 = new RiotRoomPageObjects(appiumFactory.getAndroidDriver2());
		newRoomDevice2.joinRoomButton.click();
		//send a msg from device 2 as a proof for second part
		newRoomDevice2.sendAMessage(randomMsg);
		ExplicitWait(appiumFactory.getAndroidDriver2(), newRoomDevice2.menuBackButton);
		//appiumFactory.getAndroidDriver2().pressKeyCode(AndroidKeyCode.BACK);
		newRoomDevice2.menuBackButton.click();
		//assertion on the invitee device: the new room is seen as an direct message room.
		Assert.assertTrue(roomsListDevice2.isDirectMessageByRoomName(riotuser1DisplayName),"Room "+riotuser1DisplayName+" doesn't have a little green man on invitee device.");
		/*
		 * SECOND PART
		 */
		//create a new "start" room with the same member a second time.
		roomsListDevice1.plusRoomButton.click();
		roomsListDevice1.startChatCheckedTextView.click();
		//invite a member
		inviteViewDevice1=new RiotContactPickerPageObjects(appiumFactory.getAndroidDriver1());
		inviteViewDevice1.searchAndSelectMember(riotuser2MatrixId);
		newChatViewDevice1= new RiotNewChatPageObjects(appiumFactory.getAndroidDriver1());
		newChatViewDevice1.confirmRoomCreationButton.click();
		//check that the previously create room is opened instead of being created again.
		newRoomDevice1= new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		Assert.assertEquals(newRoomDevice1.roomNameTextView.getText(), riotuser2DisplayName,"The previously created room isn't opened");
		Assert.assertEquals(newRoomDevice1.getTextViewFromPost(newRoomDevice1.getLastPost()).getText(), randomMsg,"The previously created room isn't opened");	
		newRoomDevice1.menuBackButton.click();
	}

	/**
	 * Start a chat </br>
	 * Invite more than two users </br>
	 * Check that the new room doesn't have a little green man on both devices.
	 * @throws InterruptedException 
	 */
	@Test(groups={"2drivers_android","checkuser"}, description="direct message test")
	public void startChatWithMoreThanTwoUsers() throws InterruptedException{
		String roomNameFromDevice1=riotuser2DisplayName+" and "+riotuser3DisplayName;
		
		RiotRoomsListPageObjects roomsListDevice1=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver1());
		RiotRoomsListPageObjects roomsListDevice2=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver2());
		//create a new "start chat" room
		roomsListDevice1.plusRoomButton.click();
		roomsListDevice1.startChatCheckedTextView.click();
		//invite a first member
		RiotContactPickerPageObjects inviteViewDevice1=new RiotContactPickerPageObjects(appiumFactory.getAndroidDriver1());
		inviteViewDevice1.searchAndSelectMember(riotuser2MatrixId);
		RiotNewChatPageObjects newChatViewDevice1= new RiotNewChatPageObjects(appiumFactory.getAndroidDriver1());
		newChatViewDevice1.addMemberItemListView.click();
		//invite a second member
		inviteViewDevice1.searchAndSelectMember(riotuser3MatrixId);
		newChatViewDevice1.confirmRoomCreationButton.click();

		//Room is created, then go back in rooms list
		RiotRoomPageObjects newRoomDevice1= new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		newRoomDevice1.menuBackButton.click();
		//assertion on the inviter device: the new room is NOT seen as an direct message room.
		Assert.assertFalse(roomsListDevice1.isDirectMessageByRoomName(roomNameFromDevice1),"Room "+roomNameFromDevice1+" doesn't have a little green man on inviter device.");
		//assertion the invitee device (device 2)
		//TODO: check little green man on invitation layout
		roomsListDevice2.previewInvitation(riotuser1DisplayName);
		RiotRoomPageObjects newRoomDevice2 = new RiotRoomPageObjects(appiumFactory.getAndroidDriver2());
		newRoomDevice2.joinRoomButton.click();
		ExplicitWait(appiumFactory.getAndroidDriver2(), newRoomDevice2.menuBackButton);
		newRoomDevice2.menuBackButton.click();
		//assertion on the invitee device: the new room is NOT seen as an direct message room.
		Assert.assertFalse(roomsListDevice2.isDirectMessageByRoomName(riotuser1DisplayName),"Room "+riotuser1DisplayName+" doesn't have a little green man on invitee device.");
	}

	/**
	 * Create a room </br>
	 * Invite just one user</br>
	 * Check that the new room doesn't have a little green man.
	 * @throws InterruptedException 
	 */
	@Test(groups={"2drivers_android","checkuser"}, description="direct message test")
	public void createRoomWithOneUser() throws InterruptedException{
		RiotRoomsListPageObjects roomsListDevice1=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver1());
		RiotRoomsListPageObjects roomsListDevice2=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver2());
		//create a new "start chat" room
		roomsListDevice1.plusRoomButton.click();
		roomsListDevice1.createRoomCheckedTextView.click();
		RiotRoomPageObjects newRoomDevice1 = new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		//invite 1 member
		newRoomDevice1.inviteMembersButton.click();
		RiotRoomDetailsPageObjects roomDetailsDevice1 = new RiotRoomDetailsPageObjects(appiumFactory.getAndroidDriver1());
		//add a participant from the details
		roomDetailsDevice1.addParticipant(riotuser2MatrixId);
		//go back to the rooms list
		roomDetailsDevice1.menuBackButton.click();
		newRoomDevice1.menuBackButton.click();
		//assertion on the inviter device: the new room is NOT seen as an direct message room.
		Assert.assertFalse(roomsListDevice1.isDirectMessageByRoomName(riotuser2DisplayName),"Room "+riotuser2DisplayName+" doesn't have a little green man on inviter device.");

		//assertion the invitee device (device 2)
		//TODO: check little green man on invitation layout
		roomsListDevice2.previewInvitation(riotuser1DisplayName);
		RiotRoomPageObjects newRoomDevice2 = new RiotRoomPageObjects(appiumFactory.getAndroidDriver2());
		newRoomDevice2.joinRoomButton.click();
		ExplicitWait(appiumFactory.getAndroidDriver2(), newRoomDevice2.menuBackButton);
		//appiumFactory.getAndroidDriver2().pressKeyCode(AndroidKeyCode.BACK);
		newRoomDevice2.menuBackButton.click();
		//assertion on the invitee device: the new room is NOT seen as an direct message room.
		Assert.assertFalse(roomsListDevice2.isDirectMessageByRoomName(riotuser1DisplayName),"Room "+riotuser1DisplayName+" doesn't have a little green man on invitee device.");
	}

	/**
	 * 1. Create a non direct message room.</br>
	 * 2. Tag it on DM on device 1.</br>
	 * Check that the DM tag is changed on device 1.</br>
	 * 3. Tag it On DM on device 2</br>
	 * Check that the DM tag is changed on device 2.</br>
	 * 4. UnTag it on device 1.</br>
	 * Check that the DM tag is changed on device 1.</br>
	 * 5. UnTag it on device 2.</br>
	 * Check that the DM tag is changed on device 1.</br>
	 * @throws InterruptedException 
	 */
	@Test(groups={"2drivers_android","checkuser"})
	public void tagAndUntagDirectMessageRoom() throws InterruptedException{
		RiotRoomsListPageObjects roomsListDevice1=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver1());
		RiotRoomsListPageObjects roomsListDevice2=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver2());
		//1. Create a direct message room.
		roomsListDevice1.plusRoomButton.click();
		roomsListDevice1.createRoomCheckedTextView.click();
		RiotRoomPageObjects newRoomDevice1 = new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		//put a room name
		newRoomDevice1.changeRoomName(tmpRoomName);
		//invite 1 member
		newRoomDevice1.inviteMembersButton.click();
		RiotRoomDetailsPageObjects roomDetailsDevice1 = new RiotRoomDetailsPageObjects(appiumFactory.getAndroidDriver1());
		//add a participant from the details
		roomDetailsDevice1.addParticipant(riotuser2MatrixId);
		roomDetailsDevice1.menuBackButton.click();
		newRoomDevice1.menuBackButton.click();

		//accept invitation from device 2.
		roomsListDevice2.previewInvitation(tmpRoomName);
		RiotRoomPageObjects newRoomDevice2 = new RiotRoomPageObjects(appiumFactory.getAndroidDriver2());
		newRoomDevice2.joinRoomButton.click();
		ExplicitWait(appiumFactory.getAndroidDriver2(), newRoomDevice2.menuBackButton);
		//appiumFactory.getAndroidDriver2().pressKeyCode(AndroidKeyCode.BACK);
		newRoomDevice2.menuBackButton.click();

		//2. Tag it on DM on device 1
		roomsListDevice1.clickOnContextMenuOnRoom(tmpRoomName, "Direct Chat");
		Assert.assertTrue(roomsListDevice1.isDirectMessageByRoomName(tmpRoomName),"Room "+tmpRoomName+" doesn't have a little green man on inviter device.");
		//3. Tag it on DM on device 2
		roomsListDevice2.clickOnContextMenuOnRoom(tmpRoomName, "Direct Chat");
		Assert.assertTrue(roomsListDevice2.isDirectMessageByRoomName(tmpRoomName),"Room "+tmpRoomName+" doesn't have a little green man on invitee device.");

		//4. Untag it on DM on device 1
		roomsListDevice1.clickOnContextMenuOnRoom(tmpRoomName, "Direct Chat");
		Assert.assertFalse(roomsListDevice1.isDirectMessageByRoomName(tmpRoomName),"Room "+tmpRoomName+" have a little green man on inviter device.");
		//5. Untag it on DM on device 2
		roomsListDevice2.clickOnContextMenuOnRoom(tmpRoomName, "Direct Chat");
		Assert.assertFalse(roomsListDevice2.isDirectMessageByRoomName(tmpRoomName),"Room "+tmpRoomName+" have a little green man on invitee device.");
	}

	
	@AfterMethod(alwaysRun=true)
	private void leaveRoomAfterTest(Method m) throws InterruptedException{
		switch (m.getName()) {
		case "startChatWithOneUserTwice":
			leaveRoomFromRoomsListAfterTest(riotuser2DisplayName,"Empty room");
			break;
		case "startChatWithMoreThanTwoUsers":
			leaveRoomFromRoomsListAfterTest(riotuser3DisplayName+" and "+riotuser2DisplayName,riotuser3DisplayName);
			break;
		case "createRoomWithOneUser":
			leaveRoomFromRoomsListAfterTest(riotuser2DisplayName,"Empty room");
			break;
		case "tagAndUntagDirectMessageRoom":
			leaveRoomFromRoomsListAfterTest(tmpRoomName,tmpRoomName);
			break;
		default:
			break;
		}
	}
	
	private void leaveRoomFromRoomsListAfterTest(String roomNameFromDevice1, String roomNameFromDevice2) throws InterruptedException{
		RiotRoomsListPageObjects roomsListDevice1=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver1());
		RiotRoomsListPageObjects roomsListDevice2=new RiotRoomsListPageObjects(appiumFactory.getAndroidDriver2());
		System.out.println("Leave room "+roomNameFromDevice1+ " with device 1");
		roomsListDevice1.clickOnContextMenuOnRoom(roomNameFromDevice1, "Leave Conversation");
		roomsListDevice1.alertDialogButton2.click();
		roomsListDevice1.waitUntilSpinnerDone(5);
		System.out.println("Leave room "+roomNameFromDevice2+ " with device 2");
		roomsListDevice2.clickOnContextMenuOnRoom(roomNameFromDevice2, "Leave Conversation");
		roomsListDevice2.alertDialogButton2.click();
		roomsListDevice2.waitUntilSpinnerDone(5);
		Assert.assertNull(roomsListDevice1.getRoomByName(roomNameFromDevice1), "Room "+roomNameFromDevice1+" is still displayed in the list in device 1.");
		Assert.assertNull(roomsListDevice2.getRoomByName(roomNameFromDevice2), "Room "+roomNameFromDevice2+" is still displayed in the list in device 2.");
	}
	
	/**
	 * Log the good user if not.</br> Secure the test.
	 * @param myDriver
	 * @param username
	 * @param pwd
	 * @throws InterruptedException 
	 */
	@BeforeGroups("checkuser")
	private void checkIfUserLogged() throws InterruptedException{
		super.checkIfUserLoggedAndroid(appiumFactory.getAndroidDriver1(), riotuser1DisplayName, Constant.DEFAULT_USERPWD);
		super.checkIfUserLoggedAndroid(appiumFactory.getAndroidDriver2(), riotuser2DisplayName, Constant.DEFAULT_USERPWD);
	}
	
}
