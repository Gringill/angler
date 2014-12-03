package data;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import engine.Game;
import util.Logger;
import util.PathFinder;
import util.Util;
import util.Vector2;

import java.util.ArrayList;

public class Entity extends KineticObject implements Json.Serializable {
    private ArrayList<Vector2> path;
    private PathFinder path_finder;

    private int steering = 0;
    private float leftFraction = 0;
    private float rightFraction = 0;
    private float frontFraction = 0;
    private double leftAngleTowards;
    private double rightAngleTowards;
    private boolean leftCollided;
    private boolean rightCollided;
    private boolean frontCollided;

    private RayCastCallback lrcc = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, com.badlogic.gdx.math.Vector2 point, com.badlogic.gdx.math.Vector2 normal, float fraction) {
            if (fixture.getUserData() instanceof GameObject) {
                if (((GameObject) fixture.getUserData()).getPathability() > getPathability()) {
                    System.out.println("lrcc " + fraction);
                    steering = -1;
                    Entity.this.leftFraction = fraction;
                    leftAngleTowards = new Vector2(getBody().getPosition()).getAngleTowards(new Vector2(point));
                    leftCollided = true;
                    return 0;
                }
            }
            return -1;
        }
    };
    private RayCastCallback rrcc = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, com.badlogic.gdx.math.Vector2 point, com.badlogic.gdx.math.Vector2 normal, float fraction) {
            if (fixture.getUserData() instanceof GameObject) {
                if (((GameObject) fixture.getUserData()).getPathability() > getPathability()) {
                    System.out.println("rrcc " + fraction);
                    steering = 1;
                    Entity.this.rightFraction = fraction;
                    rightAngleTowards = new Vector2(getBody().getPosition()).getAngleTowards(new Vector2(point));
                    rightCollided = true;
                    return 0;
                }
            }
            return -1;
        }
    };
    private RayCastCallback frcc = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, com.badlogic.gdx.math.Vector2 point, com.badlogic.gdx.math.Vector2 normal, float fraction) {
            if (fixture.getUserData() instanceof GameObject) {
                if (((GameObject) fixture.getUserData()).getPathability() > getPathability()) {
                    System.out.println("frcc " + fraction);
                    Entity.this.frontFraction = fraction;
                    frontCollided = true;
                    return 0;
                }
            }
            return -1;
        }
    };

    public Entity(Game game, String name, Sprite sprite, float x, float y) {
        buildAttributes();
        setGame(game);
        setSprite(sprite);
        setName(name);
        setPosition(new Vector2(x, y));
        path_finder = new PathFinder(this);
    }

    public Entity(Game game, String name, float x, float y) {
        buildAttributes();
        setGame(game);
        String file = util.Util.pullRegionFromTexture(getTexture());
        setSprite(new Sprite(getGame().getLevel().getAtlas().findRegion(file)));
        setName(name);
        setPosition(new Vector2(x, y));
        path_finder = new PathFinder(this);
    }

    public Entity(Game game) {
        buildAttributes();
        setGame(game);
        path_finder = new PathFinder(this);
    }

    public Entity() {
        buildAttributes();
    }

    public void buildAttributes() {
        myAttributes.clear();
        myAttributes.add(Attribute.ATTR_BUILDING);
        myAttributes.add(Attribute.ATTR_FACING);
        myAttributes.add(Attribute.ATTR_HEALTH);
        myAttributes.add(Attribute.ATTR_HEIGHT);
        myAttributes.add(Attribute.ATTR_MOVE_SPEED);
        myAttributes.add(Attribute.ATTR_NAME);
        myAttributes.add(Attribute.ATTR_PATHABILITY);
        myAttributes.add(Attribute.ATTR_POSITION);
        myAttributes.add(Attribute.ATTR_SIZE);
        myAttributes.add(Attribute.ATTR_TEXTURE);
        myAttributes.add(Attribute.ATTR_WIDTH);
    }

    public void update() {
        if (hasBody()) {
            setPosition((new Vector2(getBody().getPosition()).multiply(getGame().getUtil().getGameScale())));

            // RayCast Information
            steering = 0;
            leftFraction = 0;
            leftAngleTowards = 0;
            leftCollided = false;
            rightFraction = 0;
            rightAngleTowards = 0;
            rightCollided = false;
            frontFraction = 0;
            frontCollided = false;

            // Cast Rays
            Vector2 bodyPosition = new Vector2(getBody().getPosition());
            getGame().getWorld().rayCast(lrcc, bodyPosition, bodyPosition.copy().applyPolarOffset(getSize() * 2 / getGame().getUtil().getGameScale(), getFacing().rotate(0.785398)));
            getGame().getWorld().rayCast(rrcc, bodyPosition, bodyPosition.copy().applyPolarOffset(getSize() * 2 / getGame().getUtil().getGameScale(), getFacing().rotate(-0.785398)));
            getGame().getWorld().rayCast(frcc, bodyPosition, bodyPosition.copy().applyPolarOffset(getSize() * 2 / getGame().getUtil().getGameScale(), getFacing()));
        }


        if (hasPath() > 0) {
            Vector2 targetVector = path.get(0).copy();
            double distance = getPosition().copy().getDistanceTo(targetVector);

            // Determine if we have reached the targetVector
            if (distance <= getMoveSpeed()) {
                setPosition(targetVector);
                path.remove(0);
                if (path.size() == 0) {
                    path = null;
                    if (hasBody()) {
                        getBody().setLinearVelocity(new Vector2(0, 0));
                    }
                }
            }

            // Determine if the entity is still trying to get somewhere
            if (hasPath() > 0) {
                // Setup the rotation data
                double newFacing;
                double oldFacing = getFacing().angleRad();
                double angleTowards = getPosition().getAngleTowards(targetVector);
                Vector2 vectorTowards = new Vector2(1, 0);
                vectorTowards.setAngleRad((float) angleTowards);

                Logger.log("leftCollided: " + leftCollided + " rightCollided: " + rightCollided + " leftFraction: " + leftFraction + " rightFraction: " + rightFraction, "Entity Update", false);
                Logger.log("old facing: " + Math.toDegrees(oldFacing), "Entity Update", false);
                Logger.log("Angle between " + Math.toDegrees(vectorTowards.getDeltaFacing(getFacing())), "Entity Update", false);
                Logger.log("Angle towards " + angleTowards, "Entity Update", false);
                Logger.log("Target vector " + targetVector, "Entity Update", false);

                // Determine if new target angle is: within snap range, clockwise, or counterclockwise
                if (Math.abs(vectorTowards.getDeltaFacing(getFacing())) <= getTurnSpeed()) {
                    // Snap to angle
                    Logger.log("Angle Towards: " + Math.toDegrees(angleTowards) + " " + getPosition() + " " + path.get(0).copy().multiply(50f), "Entity Update", false);
                    newFacing = angleTowards;
                } else {
                    if (getFacing().getAngularRelationship(vectorTowards) == Vector2.RELATE_CLOCKWISE) {
                        Logger.log("clockwise: " + Math.toDegrees((oldFacing - getTurnSpeed())), "Entity Update", false);
                        newFacing = oldFacing + getTurnSpeed();
                    } else {
                        Logger.log("counter clockwise: " + Math.toDegrees((oldFacing + getTurnSpeed())), "Entity Update", false);
                        newFacing = oldFacing - getTurnSpeed();
                    }
                }

                // Adjust new facing against collision
                if (frontCollided) {
                    if (leftCollided) {
                        Logger.log("left collided: " + Math.toRadians(1 / leftFraction), "Entity Update", false);
                        newFacing -= Math.min(Math.toRadians(.01 / Math.pow(leftFraction, 1.6)), getTurnSpeed());
                    }

                    if (rightCollided) {
                        Logger.log("right collided: " + Math.toRadians(1 / rightFraction), "Entity Update", false);
                        newFacing += Math.min(Math.toRadians(.01 / Math.pow(rightFraction, 1.6)), getTurnSpeed());
                    }
                }

                setFacing(newFacing);
                Logger.log("New Facing: " + newFacing, "Entity Update", false);
                Logger.log("Facing As Rad: " + getFacing(), "Entity Update", false);

                if (hasBody()) {
                    Vector2 force = Util.getVelocity(getFacing().angleRad(), getMoveSpeed());
                    getBody().setLinearVelocity(force);
                    Logger.log("Force: " + force, "Entity Update", false);
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        // Draw Pathing
        if (isBuilding() && getGame().doDrawBuildingPathing()) {
            Sprite sprite = new Sprite(getGame().getLevel().getAtlas().findRegion("white_pixel"));
            sprite.setSize(getWidth(), getHeight());

            if (this.getSprite() == null) {
                batch.setColor(1, 1, 1, .3f);
                batch.draw(sprite, (getX()), (getY()), (sprite.getWidth() / 2), (sprite.getHeight() / 2), sprite.getWidth(), sprite.getHeight(), 1, 1, getFacing().angle());
            } else {
                batch.setColor(0, 1, 0, .3f);
                batch.draw(sprite, (getX()) - (sprite.getWidth() / 2), (getY()) - (sprite.getHeight() / 2), (sprite.getWidth() / 2), (sprite.getHeight() / 2), sprite.getWidth(), sprite.getHeight(),
                        1, 1, getFacing().angle());
            }

            batch.setColor(1, 1, 1, 1);

        }

        // Draw Entity
        if (getSprite() != null) {
            batch.draw(getSprite(), (getX()) - (getSprite().getWidth() / 2), (getY()) - (getSprite().getHeight() / 2), (getSprite().getWidth() / 2), (getSprite().getHeight() / 2), getSprite()
                    .getWidth(), getSprite().getHeight(), 1, 1, getFacing().angle());
        }

        // Draw Path
        if (path != null && path.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Path: ");
            for (Vector2 v : path) {
                sb.append("(" + v.x + ", " + v.y + "), ");
                batch.setColor(1, 0, 0, 1);
                batch.draw(getGame().getLevel().atlas.findRegion("white_pixel"), v.x, v.y, 1, 1, 4, 4, 1, 1, getFacing().angle());
                batch.setColor(1, 1, 1, 1);
            }
            Logger.log(sb.toString(), "Entity Draw", false);
        }
    }

    @Override
    public void drawSelection(SpriteBatch batch) {
        // TODO Auto-generated method stub

    }

    public void issuePointCommand(String command, Vector2 goal) {
        switch (command) {
            case Game.COMMAND_MOVE:
                path_finder.findThreadedPath(getPosition().multiply((1f / getGame().getUtil().getGameScale()) * getPathFinder().getNodemap().getDensity()),
                        goal.multiply((1f / getGame().getUtil().getGameScale()) * getPathFinder().getNodemap().getDensity()));
                break;
        }
    }

    @Override
    public void generatePhysicsBody() {
        if (getBody() != null) {
            getGame().getWorld().destroyBody(getBody());
        }
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(getX() / getGame().getUtil().getGameScale(), getY() / getGame().getUtil().getGameScale());
        setBody(getGame().getWorld().createBody(bodyDef));
        CircleShape circle = new CircleShape();
        circle.setRadius(getSize() / 2f / getGame().getUtil().getGameScale());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Util.CATEGORY_ENTITY;
        fixtureDef.filter.maskBits = Util.MASK_CLIP;
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        getBody().createFixture(fixtureDef);
        circle.dispose();

    }

    public void initializePathFinder() {
        path_finder = new PathFinder(this);
    }

    public float getX() {
        if (getBody() != null) {
            return getBody().getPosition().x * getGame().getUtil().getGameScale();
        }
        return getPosition().x;
    }

    public float getY() {
        if (getBody() != null) {
            return getBody().getPosition().y * getGame().getUtil().getGameScale();
        }
        return getPosition().y;
    }

    public PathFinder getPathFinder() {
        return path_finder;
    }

    public void setPosition(Vector2 newPosition) {
        super.setPosition(newPosition = newPosition.copy());
        if (getBody() != null) {
            newPosition.multiply(1f / getGame().getUtil().getGameScale());
            getBody().setAwake(true);
            getBody().setTransform(newPosition.x, newPosition.y, 0);
        }
    }

    public void setSize(float val) {
        super.setSize(val);
        if (getBody() != null) {
            generatePhysicsBody();
        }
    }

    public int hasPath() {
        return (path != null) ? path.size() : 0;
    }

    @SuppressWarnings("unchecked")
    public void setPath(ArrayList<Vector2> path) {
        this.path = path;
        // (ArrayList<Vector2>) path.clone()
    }

    /**
     * Returns a new entity defined as an exact shallow copy of this entity.
     */
    public Entity clone() {
        Entity e = new Entity(getGame());
        e.defineAs(this);
        return e;
    }

    @Override
    public void write(Json json) {
        json.writeValue("attributes", Attribute.buildAttributeList(this, myAttributes));
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        ArrayList<Attribute> list = json.readValue(ArrayList.class, jsonData.get("attributes"));
        defineAs(list);
        if (getGame() != null && getGame().getLevel() != null) {
            setSprite(new Sprite(getGame().getLevel().getAtlas().findRegion(Util.pullRegionFromTexture(getTexture()))));
        }
    }

}
