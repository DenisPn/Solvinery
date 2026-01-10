// 1. הגדרת המבנה של משתנה (Variable)
export interface ModelVariable {
  identifier: string;
  structure: string[];
  alias: string;
  objectiveValueAlias: string;
}

// 2. הגדרת המבנה של אילוץ (Constraint)
export interface ModelConstraint {
  identifier: string;
}

// 3. הגדרת המבנה של העדפה (Preference)
export interface ModelPreference {
  identifier: string;
}

// 4. הגדרת המבנה הכללי של התשובה מהשרת
export interface ParseModelResponse {
  constraints: ModelConstraint[];
  preferences: ModelPreference[];
  variables: ModelVariable[];
  
  // מילון שבו המפתח הוא שם ה-Set והערך הוא רשימת האיברים (מערך מחרוזות)
  setTypes: Record<string, string[]>; 
  
  // מילון שבו המפתח הוא שם הפרמטר והערך הוא הסוג שלו (מחרוזת בודדת)
  paramTypes: Record<string, string>; 
}

// הגדרת ה-Base URL
const API_BASE_URL = "/api";; 

export const NewImageService = {
  /**
   * שולח את קוד ה-ZPL לשרת ומקבל את ניתוח המודל
   * @param userId - מזהה המשתמש
   * @param zplCode - תוכן הקובץ (המחרוזת עצמה)
   */
  parseImageModel: async (userId: string, zplCode: string): Promise<ParseModelResponse> => {
    
    // שים לב: הנתיב כאן צריך להיות תואם בדיוק למה שיש לך ב-Swagger
    const url = `${API_BASE_URL}/user/${userId}/image/model`;

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': '*/*'
        },
        body: JSON.stringify({
          code: zplCode 
        })
      });

      if (!response.ok) {
        throw new Error(`Error parsing model: ${response.statusText}`);
      }

      const data = await response.json();
      return data as ParseModelResponse;

    } catch (error) {
      console.error("API Call Failed:", error);
      throw error;
    }
  }
};