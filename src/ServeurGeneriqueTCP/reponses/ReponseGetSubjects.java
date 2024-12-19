package ServeurGeneriqueTCP.reponses;

import MODEL.entity.Subject;

import java.util.List;

public class ReponseGetSubjects extends ReponseBSPP
{
    private List<Subject> subjects;

    public ReponseGetSubjects(boolean success, String message, List<Subject> subjects)
    {
        super(success, message);
        this.subjects = subjects;
    }

    public List<Subject> getSubjects()
    {
        return subjects;
    }
}
