package filesystem;

import static org.junit.Assert.*;



import org.junit.*;

import filesystem.exception.ItemNotWritableException;


public class TestItem {

	private static Directory directory, directory2, subdirectory, subsubdirectory, subsubdirectory2;
	
	private static File file1, file2, file3, file4, terminateFile, parentNotWritableFile;
	
	private static Link link3;
	
	@Before
	public void setUpFixture()	{
		directory2 = new Directory("AlphanonWritable",true);
		directory = new Directory("Alpha",true);
		subdirectory = new Directory(directory, "Bravo", true);
		subsubdirectory = new Directory(subdirectory, "Charlie", true);
		subsubdirectory2 = new Directory(directory2, "Delta", false);
		
		file1 = new File(directory, "file1",Type.TEXT,100, true);
		file2 = new File(directory, "file2", Type.TEXT);
		file3 = new File(subdirectory, "file3",Type.PDF,300, true);
		file4 = new File(subsubdirectory, "file4",Type.TEXT,300, true);
		terminateFile = new File(subdirectory, "terminate",Type.TEXT,300, true);
		parentNotWritableFile = new File(subsubdirectory, "notwritable",Type.TEXT,300, true);
		
		link3 = new Link(subdirectory, "linksubdir", subsubdirectory);
	}
	
	@Test 
	public void FileConstructor_LegalCase() {
		assertEquals(file1.getName(),"file1");
		assertEquals(file1.getSize(), 100);
		assertEquals(file1.getType(),Type.TEXT);
		assertEquals(file2.getName(),"file2");
		assertEquals(file2.getType(),Type.TEXT);
		assertEquals(file2.getSize(),0);
		assertEquals(file2.isWritable(),true);
		assertEquals(file3.getParentDirectory(),subdirectory);
	}
	
	@Test
	public void canBeTerminated() {
		assertTrue(file1.canBeTerminated());
		terminateFile.terminate();
		assertFalse(terminateFile.canBeTerminated());
		subsubdirectory.setReadOnly();
		assertFalse(parentNotWritableFile.canBeTerminated());
	}
	
	@Test
	public void terminate_1() {
		terminateFile.terminate();
		terminateFile.terminate();
	}
	
	@Test(expected = IllegalStateException.class)
	public void terminate_2() {	
		subdirectory.setReadOnly();
		terminateFile.terminate();
	}
	
	@Test
	public void isValidName() {
		assertTrue(file1.isValidName("HIQSDFA.AZER"));
		assertFalse(directory.isValidName("HIQSDFA.AZER"));
		assertFalse(file1.isValidName("/.#"));
	}
	
	@Test
	public void canAcceptAsNewName() {
		assertFalse(file1.canAcceptAsNewName("file1"));
		assertFalse(file1.canAcceptAsNewName("file2"));
		assertFalse(file1.canAcceptAsNewName("Bravo"));
		assertTrue(file1.canAcceptAsNewName("file3"));
	}
	
	@Test
	public void changeName() {
		file1.changeName("Hello");
		assertEquals(file1.getName(), "Hello");
		file1.changeName("file2");
		assertEquals(file1.getName(), "Hello");
	}
	
	@Test
	public void Order() {
		assertTrue(file2.isOrderedAfter(file1));
		assertTrue(directory.isOrderedBefore(subdirectory));
	}
	
	@Test
	public void hasOverlappingUserPeriod() {
		file1.move(subdirectory);
		file2.changeName("file1");
		file1.move(subsubdirectory);
		Link link4 = new Link(subsubdirectory, "hi", directory);
		assertTrue(file1.hasOverlappingUsePeriod(file2));
		assertFalse(file1.hasOverlappingUsePeriod(file3));
		assertFalse(file1.hasOverlappingUsePeriod(link4));
	}
	
	@Test
	public void BlackBoxTest_LegalCase() {
		// In this test we attempt to use our file system and look if we can't break anything
		file1.move(subdirectory);
		file2.changeName("Delta");
		subsubdirectory.move(directory);
		
	}
	
	@Test(expected = IllegalStateException.class)
	public void BlackBoxTest_IllegalMoveTermination() {
		file1.terminate();
		file1.move(subdirectory);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void BlackBoxTest_IllegalNames() {
		file1.changeName("Charlie");
		file1.move(subdirectory);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void BlackBoxTest_ParentNotWritable() {
		directory.setReadOnly();
		file1.move(subdirectory);
	}
	
	@Test //isWritable is not recursive
	public void BlackBoxTest_SuperParentNotWritable() {
		directory.setReadOnly();
		file4.move(subdirectory);
	}
	
	@Test(expected = ItemNotWritableException.class)
	public void BlackBoxTest_DirectoryNotWritable() {
		subsubdirectory2.move(directory);
	}
	
	
	
	@Test
	public void canHaveAsItem() {
		directory.setReadOnly();
		assertTrue(subdirectory.canHaveAsItem(file4));
	}
	
	@Test
	public void getAbsolutePath() {
		assertEquals(subsubdirectory.getAbsolutePath(), "/Alpha/Bravo/Charlie");
		assertEquals(file4.getAbsolutePath(), "/Alpha/Bravo/Charlie/file4.txt");
	}
	
	@Test
	public void getTotalDiskUsage() {
		assertEquals(subsubdirectory.getTotalDiskUsage(), 600);
		assertEquals(subdirectory.getTotalDiskUsage(), 1200);
		assertEquals(directory.getTotalDiskUsage(), 1300);
	}
	
	@Test
	public void deleteRecursive_subsub() {
		subsubdirectory.deleteRecursive();
		assertTrue(file4.isTerminated());
		assertFalse(link3.isTerminated());
		assertTrue(parentNotWritableFile.isTerminated());
	}
	
	@Test
	public void deleteRecursive_everything() {
		directory.deleteRecursive();
		assertTrue(file4.isTerminated());
		assertTrue(link3.isTerminated());
		assertTrue(parentNotWritableFile.isTerminated());
		assertTrue(subsubdirectory.isTerminated());
		assertTrue(file1.isTerminated());
		assertTrue(file2.isTerminated());
		assertTrue(subdirectory.isTerminated());
	}
	
	
	
	
	
	
	
}
