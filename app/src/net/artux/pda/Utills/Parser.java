package net.artux.pda.Utills;

public class Parser {
/*
    public static List<Stage> parseStages(String input){
        List<Stage> stages = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(input);
            JSONArray jsonStages = obj.getJSONArray("stages");
            for (int i = 0; i < jsonStages.length(); i++)
            {
                Stage stage = new Stage();
                JSONObject jsonStage = jsonStages.getJSONObject(i);

                stage.setId(jsonStage.getInt("id"));
                stage.setTypeStage(jsonStage.getInt("type_stage"));
                stage.setBackgroundUrl(jsonStage.getString("background_url"));
                stage.setMusicId(jsonStage.get("music_id"));
                stage.setTitle(jsonStage.getString("title"));
                stage.setMessage(jsonStage.getString("message"));
                stage.setTypeMessage(jsonStage.getInt("type_message"));

                List<Text> textList = new ArrayList<>();
                for(int j = 0; j<jsonStage.getJSONArray("texts").length(); j++){
                    Text text = new Text();
                    JSONObject jsonText  = jsonStage.getJSONArray("texts").getJSONObject(j);
                    text.text = jsonText.getString("text");
                    text.condition = jsonText.getJSONObject("condition");
                    textList.add(text);
                }
                stage.setText(textList);

                List<Transfer> transferList = new ArrayList<>();
                for(int j = 0; j<jsonStage.getJSONArray("transfers").length(); j++){
                    Transfer transfer = new Transfer();
                    JSONObject jsonTransfer  = jsonStage.getJSONArray("transfers").getJSONObject(j);
                    transfer.text = jsonTransfer.getString("text");
                    transfer.stage_id = jsonTransfer.getInt("stage_id");


                    JSONObject jsonCondition = jsonTransfer.getJSONObject("condition");
                    Conditions conditions = new Conditions();
                    List<Object> has = new ArrayList<>();
                    if(jsonCondition.has("has")) {
                        for (int h = 0; h < jsonCondition.getJSONArray("has").length(); h++) {
                            has.add(jsonCondition.getJSONArray("has").get(h));
                        }
                        jsonCondition.remove("has");
                        conditions.has = has;
                    }
                    List<Object> dsnthave = new ArrayList<>();
                    if(jsonCondition.has("!has")) {
                        for (int h = 0; h < jsonCondition.getJSONArray("!has").length(); h++) {
                            dsnthave.add(jsonCondition.getJSONArray("!has").get(h));
                        }
                        jsonCondition.remove("!has");
                        conditions.dsnthave = dsnthave;
                    }

                    Iterator<String> iterator = jsonCondition.keys();
                    List<Condition> conditionList = new ArrayList<>();
                    while(iterator.hasNext()){
                        Condition condition = new Condition();
                        int code;
                        String v = iterator.next();
                        String op = Character.toString(v.charAt(v.length()));
                        String var = v.substring(0, v.length()-1);
                        if(op.equals(">")){
                            code = 0;
                        }else if(op.equals("<")){
                            code = 1;
                        }else if(op.equals("=")){
                            code = 2;
                        }else if(op.equals("!")){
                            code = 3;
                        }else{
                            code = 0;
                            var = v;
                        }
                        condition.code = code;
                        condition.var = var;
                        condition.value = jsonCondition.get(v);

                        conditionList.add(condition);
                    }

                    transfer.conditions = conditions;

                    transferList.add(transfer);
                }
                stage.setTransfers(transferList);

                Actions actions = new Actions();
                JSONObject jsonObject = jsonStage.getJSONObject("actions");

                List<Object> items = new ArrayList<>();
                if(jsonObject.has("add")) {
                    for (int j = 0; j < jsonObject.getJSONArray("add").length(); j++) {
                        items.add(jsonObject.getJSONArray("add").get(j));
                    }
                    jsonObject.remove("add");
                    actions.setAdd_items(items);
                }

                HashMap<String, Object> values = new HashMap<>();
                if(jsonObject.has("add_values")){
                    Iterator<String> iterator = jsonObject.getJSONObject("add_values").keys();
                    while(iterator.hasNext()){
                        String var = iterator.next();
                        values.put(var, jsonObject.getJSONObject("add_values").get(var));
                    }
                    jsonObject.remove("add_values");
                    actions.setAdd_values(values);
                }

                List<Object> remove = new ArrayList<>();
                if(jsonObject.has("remove")) {
                    for (int j = 0; j < jsonObject.getJSONArray("remove").length(); j++) {
                        remove.add(jsonObject.getJSONArray("remove").get(j));
                    }
                    jsonObject.remove("remove");
                    actions.setRemove(remove);
                }

                Iterator<String> iterator = jsonObject.keys();
                List<Operation> operations = new ArrayList<>();
                while(iterator.hasNext()){
                    Operation operation = new Operation();
                    int code = 0;
                    String v = iterator.next();
                    String op = Character.toString(v.charAt(v.length()-1));
                    String var = v.substring(0, v.length()-1);
                    if(op.equals("+")){
                        code = 0;
                    }else if(op.equals("-")){
                        code = 1;
                    }else if(op.equals("*")){
                        code = 2;
                    }else {
                        code = 0;
                        var = v;
                    }
                    operation.code = code;
                    operation.var = var;
                    operation.value = jsonObject.get(v);

                    operations.add(operation);
                }
                actions.setOperations(operations);
                stage.setActions(actions);

                stages.add(stage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return stages;
    }
*/
}
