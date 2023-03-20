

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NoteRepository {
   private NoteDao noteDao;
   private LiveData<List<Note>> allNotes;

   public NoteRepository(Context context) {
       NoteDatabase database = NoteDatabase.getInstance(context);
       noteDao = database.noteDao();
       allNotes = noteDao.getAllNotes();
   }

   public void insert(Note note) {
       NoteDatabase.databaseWriteExecutor.execute(() -> {
           noteDao.insert(note);
       });
   }

   public void update(Note note) {
       NoteDatabase.databaseWriteExecutor.execute(() -> {
           noteDao.update(note);
       });
   }

   public void delete(Note note) {
       NoteDatabase.databaseWriteExecutor.execute(() -> {
           noteDao.delete(note);
       });
   }

   public void deleteAllNotes() {
       NoteDatabase.databaseWriteExecutor.execute(() -> {
           noteDao.deleteAllNotes();
       });
   }

   public LiveData<List<Note>> getAllNotes() {
       return allNotes;
   }

   public LiveData<List<Note>> searchNotes(String query) {
       return noteDao.searchNotes(query);
   }

   public LiveData<List<Note>> sortNotesByTitle() {
       return noteDao.sortNotesByTitle();
   }

   public LiveData<List<Note>> sortNotesByPriority() {
       return noteDao.sortNotesByPriority();
   }

   public void exportNotes() {
       // code for exporting notes
   }
}