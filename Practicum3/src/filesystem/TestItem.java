package filesystem;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.*;

import com.sun.javafx.scene.control.skin.FXVK.Type;

public class TestItem {

	private static Directory directory1;
	
	private static File file1, file2, file3;
	
	private static Link link1;
	
	@Before
	public void setUpFixture()	{
		file1 = new File("file1",Type.TEXT,0, true);
		file2 = new File("file2", Type.TEXT);
		file3 = File(directory1, "file3",Type.TEXT,0, true);
		
		directory1 = new Directory("Directory1",true);

		
	}
	
	@Test 
	public void FileConstructor_LegalCase() {
		assertEquals(file1.getName(),"file1");
		assertEquals(file1.getType(),Type.TEXT);
		assertEquals(file2.getName(),"file2");
		assertEquals(file2.getType(),Type.TEXT);
		assertEquals(file2.getSize(),0);
		assertEquals(file2.isWritable(),true);
		assertEquals(file3.getParentDirectory(),directory1);
	}
	
}
